package io.dodo.nav3.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/** A Navigation 2 dialog destination: a DialogFragment hosting Compose. */
class ConfirmDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Nav3ShowcaseTheme {
                ConfirmDialogContent(onYes = { dismiss() }, onNo = { dismiss() })
            }
        }
    }
}
