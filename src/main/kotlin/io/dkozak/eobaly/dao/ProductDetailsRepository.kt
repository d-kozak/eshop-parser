package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.ProductDetails
import org.springframework.data.jpa.repository.JpaRepository

interface ProductDetailsRepository : JpaRepository<ProductDetails, Long>