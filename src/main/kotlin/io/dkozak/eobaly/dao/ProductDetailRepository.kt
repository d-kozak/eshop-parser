package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.ProductDetail
import org.springframework.data.jpa.repository.JpaRepository

interface ProductDetailRepository : JpaRepository<ProductDetail, Long>