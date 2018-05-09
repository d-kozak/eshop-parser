package io.dkozak.eobaly.tasks

import io.dkozak.eobaly.dao.ErrorLogRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.ErrorLog
import io.dkozak.eobaly.domain.ProductCategory
import io.dkozak.eobaly.service.ParseShopService
import org.jboss.logging.Logger
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class EobalyParsingTask(
        private val parseEshopService: ParseShopService,
        private val productRepository: ProductRepository,
        private val errorLogRepository: ErrorLogRepository
) {

    private val log = Logger.getLogger(EobalyParsingTask::class.java)

    //    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 60)
    fun start() {
        log.info("Starting")
        val categoriesUrl = parseEshopService.parseMainPage()
        log.info("Found ${categoriesUrl.size} categories : $categoriesUrl")
        for (categoryUrl in categoriesUrl) {
            val productCategory = parseEshopService.getProductCategory(categoryUrl)

            CompletableFuture.supplyAsync {
                parseCategory(productCategory, categoryUrl)
            }
        }

    }

    private fun parseCategory(productCategory: ProductCategory, categoryUrl: String) {
        log.info("${productCategory.name} started")
        val (loadedProductUrls, failedCategoryPages) = parseEshopService.parseCategoryPage(categoryUrl, productRepository)
        for (url in loadedProductUrls) {
            val failedProductUrls = mutableListOf<String>()
            try {
                log.info("parsing $url")
                parseEshopService.parseProduct(url, productCategory)
            } catch (ex: Exception) {
                log.warn("Could not parse $url, because ${ex.message}")
                ex.printStackTrace()
                failedProductUrls += url
            } finally {
                failedProductUrls.map {
                    val errorLog = ErrorLog()
                    errorLog.type = "PRODUCT_PARSE_FAIL"
                    errorLog.data = it
                    errorLog
                }.forEach {
                    errorLogRepository.save(it)
                }
            }
        }
        failedCategoryPages.map {
            val errorLog = ErrorLog()
            errorLog.type = "CATEGORY_PAGE_FAIL"
            errorLog.data = it
            errorLog
        }.forEach {
            errorLogRepository.save(it)
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