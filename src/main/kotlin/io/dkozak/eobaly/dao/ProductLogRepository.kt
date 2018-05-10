package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.ProductLog
import org.springframework.data.jpa.repository.JpaRepository

interface ProductLogRepository : JpaRepository<ProductLog, Long> {
    fun findByAlreadyCheckedFalse(): List<ProductLog>
}
