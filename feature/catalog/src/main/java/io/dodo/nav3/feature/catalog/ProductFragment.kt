package io.dodo.nav3.feature.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/** Product detail — reads its id from the Bundle argument the list fragment passed. */
class ProductFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        val id = arguments?.getString("id").orEmpty()
        setContent {
            Nav3ShowcaseTheme {
                ProductDetailScreen(id = id, onBack = { findNavController().popBackStack() })
            }
        }
    }
}
