package io.dkozak.eobaly.domain

import java.util.*
import javax.persistence.*

@Entity
class ProductDetails {

    @GeneratedValue
    @Id
    var id = 0

    var productCount = ""

    var amount: String = ""

    var pricePerItem = 0.0

    var averagePrice = -1

    @ElementCollection
    var priceDetails = mutableListOf<String>()
    @ElementCollection
    var amountDetails = mutableListOf<String>()

    @Temporal(TemporalType.DATE)
    var timestamp = Date()

    @ManyToOne
    lateinit var product: Product

    override fun toString(): String {
        return "ProductDetails(id=$id, amount=$amount, pricePerItem=$pricePerItem)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductDetails

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}