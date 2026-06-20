package io.dodo.nav3.feature.catalog

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation keys ARE the navigation API of this feature.
 *
 * In Navigation 2 a destination was identified by a route string ("product/{id}") and arguments
 * were stringly-typed. In Navigation 3 a destination is identified by a *typed object*. The
 * compiler now guarantees you can't navigate to a product without giving it an id.
 *
 * - `NavKey` is a marker interface so the key can live on the back stack.
 * - `@Serializable` lets `rememberNavBackStack` save/restore the stack across process death.
 */
@Serializable
data object ProductList : NavKey

@Serializable
data class ProductDetail(val id: String) : NavKey
