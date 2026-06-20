package io.dodo.nav3.feature.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/**
 * Still a single Fragment in the outer Nav2 graph (the host reaches it by @id/dest_catalog), but its
 * content is now a Nav3 NavDisplay (see CatalogNavigation). The catalog is a leaf feature, so it does
 * NOT bridge out to the Nav2 NavController at all — the whole feature is self-contained on Nav3.
 *
 * This is the recommended way to START a migration: pick one feature, convert its internal navigation
 * and its overlay, and leave the rest of the Nav2 graph (home, auth, the confirm dialog) untouched.
 */
class CatalogFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Nav3ShowcaseTheme {
                CatalogNavigation()
            }
        }
    }
}
