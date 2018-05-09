package io.dkozak.eobaly.controller

import io.dkozak.eobaly.domain.ProductDetailsView
import io.dkozak.eobaly.service.ProductService
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MainRestController
(
        private val productService: ProductService
) {
    @GetMapping("/product-price/{internalName}")
    fun productPrice(model: Model, @PathVariable internalName: String): ProductDetailsView {
        return productService.loadProductDetails(internalName)
    }
}