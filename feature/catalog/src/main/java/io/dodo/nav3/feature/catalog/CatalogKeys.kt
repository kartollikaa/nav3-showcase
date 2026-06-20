package io.dodo.nav3.feature.catalog

import androidx.navigation3.runtime.NavKey
import com.github.skydoves.navgraph.annotations.NavEdge
import kotlinx.serialization.Serializable

// The catalog's internal destinations are typed Nav3 keys. @NavEdge on the list class draws its two
// outgoing transitions; ProductDetail's `id` (a serializable property) shows as a typed-arg arrow.

@NavEdge(to = ProductDetail::class, label = "Open product")
@NavEdge(to = FilterKey::class, label = "Filter")
@Serializable
data object CatalogList : NavKey

@Serializable
data class ProductDetail(val id: String) : NavKey

/** Rendered as a bottom sheet by BottomSheetSceneStrategy. */
@Serializable
data object FilterKey : NavKey
