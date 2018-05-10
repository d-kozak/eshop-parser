package io.dkozak.eobaly.tasks

import io.dkozak.eobaly.dao.ProductCategoryRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.UNKNOWN_CATEGORY
import io.dkozak.eobaly.domain.UNKOWN_PRODUCT
import org.jboss.logging.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Configuration
@Service
class DefaultDataInserter
(
        private val productRepository: ProductRepository,
        private val productCategoryRepository: ProductCategoryRepository,
        private val platformTransactionManager: PlatformTransactionManager
) : CommandLineRunner {

    private val log: Logger = Logger.getLogger(DefaultDataInserter::class.java)


    override fun run(vararg args: String) {
        val transactionTemplate = TransactionTemplate(platformTransactionManager)
        transactionTemplate.execute {
            log.info("starting")
            val unknownCategory = productCategoryRepository.findByName(UNKNOWN_CATEGORY.name)
            if (unknownCategory == null) {
                UNKNOWN_CATEGORY = productCategoryRepository.save(UNKNOWN_CATEGORY)
            }
            val unknownProduct = productRepository.findByExternalName(UNKOWN_PRODUCT.externalName)
            if (unknownProduct == null) {
                UNKOWN_PRODUCT = productRepository.save(UNKOWN_PRODUCT)
            }
            log.info("finished")
        }
    }
}