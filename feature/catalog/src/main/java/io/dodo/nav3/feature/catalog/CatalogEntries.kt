package io.dodo.nav3.feature.catalog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.dodo.nav3.feature.catalog.scene.BottomSheetSceneStrategy

/**
 * The catalog feature's contribution to the ROOT back stack. Its `list → detail` navigation and its
 * filter bottom sheet are now entries on the one shared stack — no host Fragment, no NavDisplay of its
 * own. The filter is an overlay scene via the copied-in `BottomSheetSceneStrategy`.
 */
@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.catalogEntries(
    backStack: NavBackStack<NavKey>,
) {
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
}
