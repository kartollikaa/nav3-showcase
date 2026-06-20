package io.dodo.nav3.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.dodo.nav3.core.designsystem.R
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/**
 * Boilerplate that every Compose-in-fragment screen repeats: make a ComposeView, set the disposal
 * strategy, host the theme, then bridge clicks to the NavController. Cross-module navigation uses
 * shared destination resource IDs (declared in :core:designsystem, see navigation_ids.xml) — every
 * module can see them, so we navigate by R.id instead of implicit deep-link URI strings.
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
                    onOpenAuth = { findNavController().navigate(R.id.dest_auth) },
                    onOpenPromo = { findNavController().navigate(R.id.dest_promo) },
                    onConfirm = { findNavController().navigate(R.id.dest_confirm) },
                )
            }
        }
    }
}
