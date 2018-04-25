package io.dkozak.eobaly.domain

import javax.persistence.*

@Entity
class Product {
    @Id
    @GeneratedValue
    var id = 0
    var name = ""
    @OneToMany(mappedBy = "product")
    var details: List<ProductDetails> = emptyList()

    @ManyToOne
    lateinit var category: ProductCategory

    override fun toString(): String {
        return "Product(id=$id, name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }


}