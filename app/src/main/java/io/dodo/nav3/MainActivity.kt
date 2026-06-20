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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.dodo.nav3.core.designsystem.ContentYellow
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme
import io.dodo.nav3.feature.catalog.Filter
import io.dodo.nav3.feature.catalog.FilterSheet
import io.dodo.nav3.feature.catalog.ProductDetail
import io.dodo.nav3.feature.catalog.ProductDetailScreen
import io.dodo.nav3.feature.catalog.ProductList
import io.dodo.nav3.feature.catalog.ProductListScreen
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

/**
 * Same single back stack as branch 1, but now NavDisplay is given SCENE STRATEGIES. A scene strategy
 * looks at the back stack and decides the *layout*: an overlay (bottom sheet) or a two-pane list-detail
 * on wide screens. Crucially, the navigation model didn't change — only how entries are arranged did.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CatalogNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(ProductList)

    // Material-provided adaptive scene: shows list + detail side-by-side when wide, single pane when narrow.
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    // Our copied-in recipe (see scene/BottomSheetSceneStrategy.kt).
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        // Strategies are tried in order; the first to return a Scene wins. Overlays (bottom sheet) go
        // first so they layer ON TOP of whatever list/detail layout is underneath. If none match,
        // NavDisplay falls back to SinglePaneSceneStrategy automatically.
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
                ProductDetailScreen(id = key.id, onBack = { backStack.removeLastOrNull() })
            }
            entry<Filter>(
                metadata = BottomSheetSceneStrategy.bottomSheet(),
            ) {
                FilterSheet(onApply = { backStack.removeLastOrNull() })
            }
        },
    )
}
