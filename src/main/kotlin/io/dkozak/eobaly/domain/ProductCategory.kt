package io.dkozak.eobaly.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class ProductCategory(name: String) {

    @Id
    @GeneratedValue
    var id = 0

    var name = ""

    @OneToMany(mappedBy = "category")
    var products: List<Product> = emptyList()

    init {
        this.name = name
    }
}