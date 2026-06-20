package io.dodo.nav3.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/**
 * Boilerplate that every Compose-in-fragment screen repeats: make a ComposeView, set the disposal
 * strategy, host the theme, then bridge clicks to the NavController. Cross-module navigation goes
 * through implicit deep-link URIs because action IDs live in another module's R class.
 */
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Nav3ShowcaseTheme {
                HomeScreen(
                    onOpenAuth = { findNavController().navigate("app://auth".toUri()) },
                    onOpenPromo = { findNavController().navigate("app://promo".toUri()) },
                    onConfirm = { findNavController().navigate("app://confirm".toUri()) },
                )
            }
        }
    }
}
