package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>