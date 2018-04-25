package io.dkozak.eobaly.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class PriceDetails {

    @GeneratedValue
    @Id
    var id = 0

    var amount = 0

    var pricePerItem = 0.0

    @ManyToOne
    lateinit var productDetails: ProductDetails

    override fun toString(): String {
        return "PriceDetails(id=$id, amount=$amount, pricePerItem=$pricePerItem)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PriceDetails

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}