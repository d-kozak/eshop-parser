package io.dkozak.eobaly.controller

import io.dkozak.eobaly.dao.ProductRepository
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.regex.Pattern

@RestController
class MainRestController
(
        private val productRepository: ProductRepository
) {
    @GetMapping("/product-price/{internalName}")
    fun productPrice(model: Model, @PathVariable internalName: String): List<Pair<Date, Double>> {
        val product = productRepository.findByInternalName(internalName)
        if (product == null) {
            throw RuntimeException("product not found")
        }
        return product.details.map {
            it.timestamp to parsePrice(it.priceDetails)
        }
    }


    private fun parsePrice(priceDetails: List<String>): Double =
            priceDetails.map {
                val pattern = Pattern.compile(""".*(\d)+.*""")
                pattern.matcher(it).group(1).toDouble()
            }.average()
}