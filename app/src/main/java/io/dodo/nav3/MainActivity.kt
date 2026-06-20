package io.dodo.nav3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.dodo.nav3.core.designsystem.ContentYellow
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme
import io.dodo.nav3.feature.catalog.CatalogGraph
import io.dodo.nav3.feature.catalog.Filter
import io.dodo.nav3.feature.catalog.FilterSheet
import io.dodo.nav3.feature.catalog.ProductDetail
import io.dodo.nav3.feature.catalog.ProductDetailScreen
import io.dodo.nav3.feature.catalog.ProductDetailViewModel
import io.dodo.nav3.feature.catalog.ProductList
import io.dodo.nav3.feature.catalog.ProductListScreen
import io.dodo.nav3.feature.catalog.nextProductId
import io.dodo.nav3.scene.BottomSheetSceneStrategy

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Nav3ShowcaseTheme {
                Scaffold { padding ->
                    CatalogNavigation(Modifier.padding(padding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CatalogNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(ProductList)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        // ── The decorators are the new thing on this branch ──────────────────────────────────────
        // Decorators wrap EVERY NavEntry. Order matters.
        //   1) SaveableStateHolder must be first: it makes rememberSaveable {} inside a screen work,
        //      keyed per entry, across config change & process death.
        //   2) ViewModelStore gives each NavEntry its OWN ViewModelStoreOwner. Now a viewModel()
        //      created inside an entry is scoped to that entry: created on push, cleared on pop.
        // Remove the second line and ViewModels fall back to the Activity scope — see BRANCH.md.
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        sceneStrategies = listOf(bottomSheetStrategy, listDetailStrategy),
        entryProvider = entryProvider {
            entry<ProductList>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = { ContentYellow("Choose a product from the list") },
                ),
            ) {
                ProductListScreen(
                    onProductClick = { id -> backStack.add(ProductDetail(id)) },
                    onOpenFilter = { backStack.add(Filter) },
                )
            }
            entry<ProductDetail>(
                metadata = ListDetailSceneStrategy.detailPane(),
            ) { key ->
                // viewModel() resolves against the per-entry ViewModelStoreOwner that the decorator
                // installed. The initializer is our manual-DI construction (the "Dagger seam").
                val vm: ProductDetailViewModel = viewModel {
                    ProductDetailViewModel(
                        repository = CatalogGraph.productRepository,
                        productId = key.id,
                    )
                }
                ProductDetailScreen(
                    product = vm.product,
                    clicks = vm.clicks,
                    debugLabel = vm.debugLabel,
                    onIncrement = vm::onClick,
                    onOpenAnother = { backStack.add(ProductDetail(nextProductId(key.id))) },
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<Filter>(
                metadata = BottomSheetSceneStrategy.bottomSheet(),
            ) {
                FilterSheet(onApply = { backStack.removeLastOrNull() })
            }
        },
    )
}
