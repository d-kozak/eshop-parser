package io.dkozak.eobaly.dao

import io.dkozak.eobaly.domain.ProductLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductLogRepository : JpaRepository<ProductLog, Long> {
    fun findByAlreadyCheckedFalse(): List<ProductLog>

    @Query("select max(log.executionId) + 1 from ProductLog log")
    fun findNextExecutionId(): Long?
}
