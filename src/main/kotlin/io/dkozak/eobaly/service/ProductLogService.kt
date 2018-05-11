package io.dkozak.eobaly.service

import io.dkozak.eobaly.dao.ProductLogRepository
import io.dkozak.eobaly.domain.ProductLog
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class ProductLogService(
        private val productLogRepository: ProductLogRepository
) {

    fun loadAllNewLogs(): List<ProductLog> {
        val newLogs = productLogRepository.findByAlreadyCheckedFalse()
        for (productLog in newLogs) {
            productLog.alreadyChecked = true
        }
        return newLogs
    }

    fun findAll() = productLogRepository.findAll()

    fun save(productLog: ProductLog) = productLogRepository.save(productLog)
}

