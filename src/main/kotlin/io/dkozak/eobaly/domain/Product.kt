package io.dkozak.eobaly.domain

import javax.persistence.*
import javax.validation.constraints.NotNull

@Volatile
var UNKOWN_PRODUCT = Product().let {
    it.category = UNKNOWN_CATEGORY
    it.externalName = "UNKNOWN"
    it.internalName = "UNKNOWN"
    it.url = "pool/vzor/products/clim_thumb_xxl_103841_1.jpg"
    it
}

@Entity
class Product {
    @Id
    @GeneratedValue
    var id = 0
    var url = ""
    @Column(unique = true)
    @NotNull
    var internalName: String = ""
    var externalName: String = ""

    var imgUrl = "pool/vzor/products/clim_thumb_xxl_103841_1.jpg"

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    var details: MutableList<ProductDetails> = mutableListOf()

    @ManyToOne(fetch = FetchType.EAGER)
    lateinit var category: ProductCategory

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

    override fun toString(): String {
        return "Product(id=$id, url='$url', internalName='$internalName', externalName='$externalName')"
    }
}