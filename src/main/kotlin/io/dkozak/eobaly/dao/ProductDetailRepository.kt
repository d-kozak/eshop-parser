package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.ProductDetails
import org.springframework.data.jpa.repository.JpaRepository

interface ProductDetailRepository : JpaRepository<ProductDetails, Long>