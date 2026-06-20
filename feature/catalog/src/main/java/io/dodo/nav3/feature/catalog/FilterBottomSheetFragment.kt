package io.dodo.nav3.feature.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/** The catalog's filter, the Navigation 2 way: a Material BottomSheetDialogFragment as a <dialog> dest. */
class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Nav3ShowcaseTheme {
                FilterSheet(onApply = { dismiss() })
            }
        }
    }
}
