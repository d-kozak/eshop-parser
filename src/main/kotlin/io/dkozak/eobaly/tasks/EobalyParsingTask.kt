package io.dkozak.eobaly.tasks

import io.dkozak.eobaly.dao.ErrorLogRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.ErrorLog
import io.dkozak.eobaly.domain.ProductCategory
import io.dkozak.eobaly.service.ParseShopService
import io.dkozak.eobaly.utils.stackTraceAsString
import org.jboss.logging.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class EobalyParsingTask(
        private val parseEshopService: ParseShopService,
        private val productRepository: ProductRepository,
        private val errorLogRepository: ErrorLogRepository
) {

    private val log = Logger.getLogger(EobalyParsingTask::class.java)

    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 60)
    fun start() {
        log.info("Starting")
        val categoriesUrl = parseEshopService.parseMainPage()
        log.info("Found ${categoriesUrl.size} categories : $categoriesUrl")
        for (categoryUrl in categoriesUrl) {
            val productCategory = parseEshopService.getProductCategory(categoryUrl)

            CompletableFuture.supplyAsync {
                parseCategory(productCategory, categoryUrl)
            }.exceptionally {
                val log = ErrorLog()
                log.type = "WHOLE_CATEGORY_ERROR"
                log.url = categoryUrl
                log.message = it.message ?: ""
                log.stackTrace = it.stackTraceAsString()

                errorLogRepository.save(log)
            }
        }
        log.info("Finished")
    }

    private fun parseCategory(productCategory: ProductCategory, categoryUrl: String) {
        log.info("${productCategory.name} started")
        val loadedProductUrls = parseEshopService.parseCategoryPage(categoryUrl, productRepository)
        for (url in loadedProductUrls) {
            try {
                log.info("parsing $url")
                parseEshopService.parseProduct(url, productCategory)
            } catch (ex: Exception) {
                log.warn("Could not parse $url, because ${ex.message}")
                ex.printStackTrace()
                val log = ErrorLog()
                log.url = url;
                log.message = ex.message ?: ""
                log.type = "PRODUCT_PAGE_FAIL"
                log.stackTrace = ex.stackTraceAsString()
                errorLogRepository.save(log)
            }
        }
        log.info("${productCategory.name} finished")
    }

    fun startFor(internalName: String) {
        CompletableFuture.supplyAsync {
            val url = "https://www.eobaly.cz/${internalName}.htm"
            val parsedProduct = parseEshopService.parseProduct(url)
            productRepository.save(parsedProduct)
        }
    }
}