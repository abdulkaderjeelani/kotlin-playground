package hello

import arrow.higherkind
import arrow.optics.optics
import arrow.optics.dsl.*
import arrow.optics.Optional

@optics
data class Product(val id: ProductId) {
    companion object {}
}

@optics
data class ProductId(val identifier: String, val version: String) {
    companion object {}
}


fun main() {

    val apple = Product(ProductId("apple", "1"))
    println(apple)

    val orange: Product = Product.id.identifier.modify(apple) {
        "orange"
    }
    println(orange)
}
