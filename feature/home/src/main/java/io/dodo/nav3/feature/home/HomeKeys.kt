package io.dodo.nav3.feature.home

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

/** Rendered as a dialog (DialogSceneStrategy) on the root back stack — the last overlay to migrate. */
@Serializable
data object ConfirmKey : NavKey
