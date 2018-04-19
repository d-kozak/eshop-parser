package io.dkozak.eobaly.domain


import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class ProductDetail {

    @Id
    @GeneratedValue
    var id = 0

    var price = 0
    var amount = 0
    var timestamp = Date()

    @ManyToOne
    lateinit var product: Product
}