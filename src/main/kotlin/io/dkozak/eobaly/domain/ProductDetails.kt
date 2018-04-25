package io.dkozak.eobaly.domain


import java.util.*
import javax.persistence.*

@Entity
class ProductDetails {

    @Id
    @GeneratedValue
    var id = 0

    var internalName: String = ""

    var externalName: String = ""

    var productCount = 0

    @ManyToOne
    lateinit var product: Product

    @OneToMany
    var priceDetails = emptyList<PriceDetails>()

    var price = 0

    @Temporal(TemporalType.TIMESTAMP)
    var timestamp = Date()
}