package io.dodo.nav3.feature.home

import androidx.navigation3.runtime.NavKey
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavGraphRoot
import kotlinx.serialization.Serializable

// The keys ARE the graph. Annotating the route classes lets the navgraph KSP processor draw the flow
// map. Intra-feature edges live here; cross-feature edges (home -> catalog, home -> auth, name ->
// home) are declared in :app (see RootNavigation), the only module that sees every feature's keys.

@NavGraphRoot                                              // the start destination
@NavEdge(to = ConfirmKey::class, label = "Confirm order")  // from = HomeKey (the annotated class)
@Serializable
data object HomeKey : NavKey

/** Rendered as a dialog (DialogSceneStrategy) on the root back stack — the last overlay to migrate. */
@Serializable
data object ConfirmKey : NavKey
