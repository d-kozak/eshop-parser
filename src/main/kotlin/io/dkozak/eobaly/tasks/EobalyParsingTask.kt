package io.dkozak.eobaly.tasks

import io.dkozak.eobaly.dao.ErrorLogRepository
import io.dkozak.eobaly.dao.ProductLogRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.ErrorLog
import io.dkozak.eobaly.domain.ProductCategory
import io.dkozak.eobaly.domain.ProductLog
import io.dkozak.eobaly.service.MAIN_URL
import io.dkozak.eobaly.service.ParseEshopService
import io.dkozak.eobaly.utils.stackTraceAsString
import org.jboss.logging.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class EobalyParsingTask(
        private val parseEshopService: ParseEshopService,
        private val productRepository: ProductRepository,
        private val errorLogRepository: ErrorLogRepository,
        private val productLogRepository: ProductLogRepository
) {

    private val log = Logger.getLogger(EobalyParsingTask::class.java)

    private val lock = Any()
    private var running = false

    @Scheduled(initialDelay = 1000 * 60 * 60 * 24, fixedDelay = 1000 * 60 * 60 * 24)
    fun start() {
        log.info("Starting")
        synchronized(lock) {
            if (running) {
                log.warn("Another task is already running")
                return
            }
            running = true
        }
        try {
            val executionId = productLogRepository.findNextExecutionId() ?: 0
            val categoriesUrl = parseEshopService.parseMainPage()
                    .map { if (it.startsWith(MAIN_URL)) MAIN_URL + it else it }
            log.info("Found ${categoriesUrl.size} categories : $categoriesUrl")
            for (categoryUrl in categoriesUrl) {
                val productCategory = parseEshopService.getProductCategory(categoryUrl, executionId)

                CompletableFuture.supplyAsync {
                    parseCategory(productCategory, categoryUrl, executionId)
                }.exceptionally {
                    val log = ErrorLog()
                    log.type = "WHOLE_CATEGORY_ERROR"
                    log.url = categoryUrl
                    log.message = it.message ?: ""
                    log.stackTrace = it.stackTraceAsString()

                    errorLogRepository.save(log)
                }
            }
        } finally {
            synchronized(lock) {
                running = false
            }
            log.info("Finished")
        }
    }

    fun parseCategory(productCategory: ProductCategory, categoryUrl: String, executionId: Long = -1) {
        log.info("${productCategory.name} started")
        val loadedProductUrls = parseEshopService.parseCategoryPage(categoryUrl, productRepository, executionId)
        for (url in loadedProductUrls) {
            try {
                log.info("parsing $url")
                parseEshopService.parseProduct(url, productCategory)
                productLogRepository.save(ProductLog(url = url, state = "SUCCESS", executionId = executionId))
            } catch (ex: Exception) {
                log.warn("Could not parse $url, because ${ex.message}")
                ex.printStackTrace()
                val log = ErrorLog()
                log.url = url;
                log.message = ex.message ?: ""
                log.type = "PRODUCT_PAGE_FAIL"
                log.stackTrace = ex.stackTraceAsString()
                errorLogRepository.save(log)
                productLogRepository.save(ProductLog(url = url, state = "FAILED", executionId = executionId))
            }
        }
        log.info("${productCategory.name} finished")
    }

    fun startFor(internalName: String) {
        CompletableFuture.supplyAsync {
            val url = "https://www.eobaly.cz/${internalName}.htm"
            val (parsedProduct, _) = parseEshopService.parseProduct(url)
            productRepository.save(parsedProduct)
        }
    }
}