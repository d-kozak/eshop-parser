package io.dkozak.eobaly.controller

import io.dkozak.eobaly.domain.Product
import io.dkozak.eobaly.domain.ProductDetailsView
import io.dkozak.eobaly.domain.ProductLog
import io.dkozak.eobaly.service.DataGenerationService
import io.dkozak.eobaly.service.ParseEshopService
import io.dkozak.eobaly.service.ProductLogService
import io.dkozak.eobaly.service.ProductService
import io.dkozak.eobaly.tasks.EobalyParsingTask
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

@RestController
class MainRestController
(
        private val productService: ProductService,
        private val parseEshopService: ParseEshopService,
        private val productLogService: ProductLogService,
        private val dataGenerationService: DataGenerationService,
        private val eobalyParsingTask: EobalyParsingTask
) {
    @GetMapping("/product-price/{internalName}")
    fun productPrice(model: Model, @PathVariable internalName: String): ProductDetailsView {
        return productService.loadProductDetails(internalName)
    }

    @GetMapping("/logs")
    fun productLogs(): List<ProductLog> = productLogService.loadAllNewLogs()

    @GetMapping("/logs/all")
    fun productLogsAll(): List<ProductLog> = productLogService.findAll()


    @GetMapping("/parse/all")
    fun parseAll(): String {
        CompletableFuture.supplyAsync(eobalyParsingTask::start)
        return "\"ok\""
    }

    @PostMapping("/parse/category/")
    fun parseCategory(@RequestBody url: String): String {
        val productCategory = parseEshopService.getProductCategory(url)
        CompletableFuture.supplyAsync {
            eobalyParsingTask.parseCategory(productCategory, url)
        }
        return "\"ok\""
    }

    @PostMapping("/parse/product/")
    fun parseProduct(@RequestBody url: String): Product = productService.parseProduct(url)

    @GetMapping("/generate")
    fun generate(): String {
        dataGenerationService.generatePriceForAll()
        return "\"ok\""
    }
}