package io.dkozak.eobaly.tasks

import io.dkozak.eobaly.dao.ProductCategoryRepository
import io.dkozak.eobaly.dao.ProductDetailsRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.ProductCategory
import io.dkozak.eobaly.service.ParseShopService
import io.dkozak.eobaly.service.parseNameFromUrl
import org.jboss.logging.Logger
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class EobalyParsingTask(
        private val parseEshopService: ParseShopService,
        private val productRepository: ProductRepository,
        private val productDetailsRepository: ProductDetailsRepository,
        private val productCategoryRepository: ProductCategoryRepository
) {

    private val log = Logger.getLogger(EobalyParsingTask::class.java)

    //    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 60)
    fun start() {
        log.info("Starting")
        val categoriesUrl = parseEshopService.parseMainPage()
        log.info("Found ${categoriesUrl.size} categories : $categoriesUrl")
        for (categoryUrl in categoriesUrl) {
            val productCategory = ProductCategory()
            productCategory.name = parseNameFromUrl(categoryUrl)
            productCategory.url = categoryUrl
            productCategoryRepository.save(productCategory)

            CompletableFuture.supplyAsync {
                log.info("${productCategory.name} started")
                val urls: Pair<List<String>, List<String>> = parseEshopService.parseCategoryPage(categoryUrl, productRepository)
                for (url in urls.first) {
                    val failedProducts = mutableListOf<String>()
                    try {
                        log.info("parsing $url")
                        val product = parseEshopService.parseProduct(url)
                        product.category = productCategory
                        productDetailsRepository.save(product.details[0])
                        productRepository.save(product)
                    } catch (ex: Exception) {
                        log.warn("Could not parse $url, because ${ex.message}")
                        ex.printStackTrace()
                        failedProducts += url
                    }
                }
            }.thenAccept {
                log.info("${productCategory.name} finished")
            }

        }

    }
}