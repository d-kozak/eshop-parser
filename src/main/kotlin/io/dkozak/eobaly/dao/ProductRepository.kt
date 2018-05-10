package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.Product
import io.dkozak.eobaly.domain.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {

    fun findByCategory(category: ProductCategory): List<Product>
    fun findByInternalName(internalName: String): Product?
    fun findByExternalNameLike(externalName: String): List<Product>
    fun findByCategoryAndExternalNameLike(category: ProductCategory, externalName: String): List<Product>
}