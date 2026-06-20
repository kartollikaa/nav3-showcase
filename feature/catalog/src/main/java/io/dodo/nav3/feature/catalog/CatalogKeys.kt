package io.dodo.nav3.feature.catalog

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// The catalog's internal destinations are now typed Nav3 keys on a back stack we own — no more
// Fragments, XML actions, or Bundle args inside the feature.

@Serializable
data object CatalogList : NavKey

@Serializable
data class ProductDetail(val id: String) : NavKey

/** Rendered as a bottom sheet by the local NavDisplay (replacing FilterBottomSheetFragment). */
@Serializable
data object FilterKey : NavKey
