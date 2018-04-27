package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository


interface ProductCategoryRepository : JpaRepository<ProductCategory, Long> {
    fun findByName(name: String): ProductCategory?
}