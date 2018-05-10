package io.dkozak.eobaly.controller

import io.dkozak.eobaly.domain.ProductDetailsView
import io.dkozak.eobaly.domain.ProductLog
import io.dkozak.eobaly.service.ProductLogService
import io.dkozak.eobaly.service.ProductService
import io.dkozak.eobaly.tasks.EobalyParsingTask
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
class MainRestController
(
        private val productService: ProductService,
        private val productLogService: ProductLogService,
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
}