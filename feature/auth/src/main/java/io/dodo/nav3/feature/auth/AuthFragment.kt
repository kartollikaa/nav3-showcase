package io.dodo.nav3.feature.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/**
 * The single Fragment that used to be three. It hosts the Nav3 auth back stack. The ONLY interaction
 * with the outer Nav2 world is exiting the feature when the flow completes.
 */
class AuthFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Nav3ShowcaseTheme {
                AuthNavigation(
                    onAuthComplete = { phone, name ->
                        Log.d("Nav3Auth", "completed: phone=$phone name=$name")
                        // Exit the auth feature; Nav2 returns us to Home.
                        findNavController().popBackStack()
                    },
                )
            }
        }
    }
}
