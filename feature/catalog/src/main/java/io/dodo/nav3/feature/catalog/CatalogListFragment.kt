package io.dodo.nav3.feature.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/**
 * Catalog list — a Fragment hosting a ComposeView. Internal navigation (list -> product,
 * list -> filter) uses THIS module's own resource IDs, since they live in this module's R class.
 * The product id rides along as a Bundle argument (Nav2 has no type-safe args without Safe Args).
 */
class CatalogListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Nav3ShowcaseTheme {
                ProductListScreen(
                    onProductClick = { id ->
                        findNavController().navigate(R.id.action_catalogList_to_product, bundleOf("id" to id))
                    },
                    onOpenFilter = { findNavController().navigate(R.id.filterBottomSheetFragment) },
                )
            }
        }
    }
}
