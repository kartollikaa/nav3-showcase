package io.dodo.nav3.feature.catalog

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ProductList : NavKey

@Serializable
data class ProductDetail(val id: String) : NavKey

/** A destination that we will render as a bottom sheet (see the scene strategy in :app). */
@Serializable
data object Filter : NavKey
