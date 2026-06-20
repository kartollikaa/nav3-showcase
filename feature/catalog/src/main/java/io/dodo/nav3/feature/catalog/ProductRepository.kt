package io.dodo.nav3.feature.catalog

data class Product(val id: String, val name: String, val description: String)

interface ProductRepository {
    fun load(id: String): Product
}

class InMemoryProductRepository : ProductRepository {
    override fun load(id: String): Product = Product(
        id = id,
        name = id.replaceFirstChar { it.uppercase() },
        description = "A delicious $id, loaded by ${this::class.simpleName}.",
    )
}

/** Cycle to a different product id, for the "open another product" button. */
fun nextProductId(current: String): String {
    val ids = listOf("apple", "banana", "cherry")
    val i = ids.indexOf(current)
    return ids[(if (i == -1) 0 else i + 1) % ids.size]
}
