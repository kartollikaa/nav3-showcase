package io.dodo.nav3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme
import io.dodo.nav3.feature.auth.NameKey
import io.dodo.nav3.feature.auth.PhoneKey
import io.dodo.nav3.feature.auth.SmsKey
import io.dodo.nav3.feature.auth.authEntries
import io.dodo.nav3.feature.catalog.CatalogList
import io.dodo.nav3.feature.catalog.catalogEntries
import io.dodo.nav3.feature.catalog.scene.BottomSheetSceneStrategy
import io.dodo.nav3.feature.home.HomeKey
import io.dodo.nav3.feature.home.homeEntries

/**
 * The migration's end state: a single Activity, a single root NavDisplay, one back stack for the whole
 * app. No Fragments, no FragmentManager, no Navigation 2, no XML nav graphs.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Nav3ShowcaseTheme {
                Scaffold { padding ->
                    RootNavigation(Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun RootNavigation(modifier: Modifier = Modifier) {
    // ONE back stack for the entire app. Home, the catalog, the auth steps, and every overlay live here.
    val backStack = rememberNavBackStack(HomeKey)
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        // Overlays: the catalog filter (bottom sheet) and the confirm dialog. DialogSceneStrategy is
        // built into the core library; BottomSheetSceneStrategy is the copied-in recipe (owned by catalog).
        sceneStrategies = listOf(bottomSheetStrategy, DialogSceneStrategy()),
        entryProvider = entryProvider {
            // Each feature contributes its entries. The app is the only module that sees all of them,
            // so cross-feature navigation (home -> catalog, home -> auth) is wired here as lambdas.
            homeEntries(
                backStack = backStack,
                onOpenCatalog = { backStack.add(CatalogList) },
                onOpenAuth = { backStack.add(PhoneKey) },
            )
            catalogEntries(backStack = backStack)
            authEntries(
                backStack = backStack,
                onComplete = { _, _ ->
                    // Drop the auth sub-flow from the top of the stack, returning to Home.
                    while (backStack.lastOrNull().let { it is PhoneKey || it is SmsKey || it is NameKey }) {
                        backStack.removeLastOrNull()
                    }
                },
            )
        },
    )
}
