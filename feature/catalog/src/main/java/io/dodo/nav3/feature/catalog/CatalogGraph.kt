package io.dodo.nav3.feature.catalog

/**
 * The "Dagger seam". This project uses manual DI to keep the talk about navigation, but this object
 * is exactly where a Dagger-provided dependency would arrive in production:
 *
 *   class ProductDetailViewModel @Inject constructor(
 *       private val repository: ProductRepository,
 *       @Assisted private val productId: String,   // via AssistedInject
 *   ) : ViewModel()
 *
 * Here we hand-wire the same graph. The ViewModel's *lifecycle* (when it's created and cleared) is
 * controlled by Navigation 3, not by Dagger — that's the point of this branch.
 */
object CatalogGraph {
    val productRepository: ProductRepository by lazy { InMemoryProductRepository() }
}
