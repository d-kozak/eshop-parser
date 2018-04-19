package io.dkozak.eobaly.domain

import javax.persistence.*

@Entity
class Product {
    @Id
    @GeneratedValue
    var id = 0
    var name = ""
    @OneToMany(mappedBy = "product")
    var details: List<ProductDetail> = emptyList()

    @ManyToOne
    lateinit var category: ProductCategory
}