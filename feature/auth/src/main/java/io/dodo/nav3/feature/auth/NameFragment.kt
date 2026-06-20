package io.dodo.nav3.feature.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

class NameFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Nav3ShowcaseTheme {
                    // Finishing auth pops the whole nested graph (phone/sms/name), returning to Home.
                    NameScreen(onFinish = { findNavController().popBackStack(R.id.phoneFragment, true) })
                }
            }
        }
}
