package io.dodo.nav3.feature.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy

/**
 * The home feature's contribution to the ROOT back stack.
 *
 * Instead of a Fragment + an XML graph, a feature now exposes an `EntryProviderScope<NavKey>`
 * extension that the app calls inside its single `entryProvider { }`. Home owns its confirm dialog
 * (a `DialogSceneStrategy` scene); the cross-feature jumps (catalog, auth) are lambdas, so home never
 * depends on another feature module.
 */
fun EntryProviderScope<NavKey>.homeEntries(
    backStack: NavBackStack<NavKey>,
    onOpenCatalog: () -> Unit,
    onOpenAuth: () -> Unit,
) {
    entry<HomeKey> {
        HomeScreen(
            onOpenCatalog = onOpenCatalog,
            onOpenAuth = onOpenAuth,
            onConfirm = { backStack.add(ConfirmKey) },
        )
    }
    entry<ConfirmKey>(metadata = DialogSceneStrategy.dialog()) {
        ConfirmDialogContent(
            onYes = { backStack.removeLastOrNull() },
            onNo = { backStack.removeLastOrNull() },
        )
    }
}
