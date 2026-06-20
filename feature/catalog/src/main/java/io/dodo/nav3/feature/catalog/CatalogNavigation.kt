package io.dodo.nav3.feature.catalog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.dodo.nav3.feature.catalog.scene.BottomSheetSceneStrategy

/**
 * A LOCAL Navigation 3 island living inside CatalogFragment — the first piece migrated off Nav2.
 *
 * `CatalogList → ProductDetail(id)` is an owned back stack (forward = `add`, back = `removeLastOrNull`),
 * and the filter is a Nav3 destination rendered as an overlay by `BottomSheetSceneStrategy`. No XML
 * actions, no Bundle args, no BottomSheetDialogFragment — the typed `ProductDetail(id)` carries the id.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogNavigation() {
    val backStack = rememberNavBackStack(CatalogList)
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(bottomSheetStrategy),
        entryProvider = entryProvider {
            entry<CatalogList> {
                ProductListScreen(
                    onProductClick = { id -> backStack.add(ProductDetail(id)) },
                    onOpenFilter = { backStack.add(FilterKey) },
                )
            }
            entry<ProductDetail> { key ->
                ProductDetailScreen(id = key.id, onBack = { backStack.removeLastOrNull() })
            }
            entry<FilterKey>(metadata = BottomSheetSceneStrategy.bottomSheet()) {
                FilterSheet(onApply = { backStack.removeLastOrNull() })
            }
        },
    )
}
