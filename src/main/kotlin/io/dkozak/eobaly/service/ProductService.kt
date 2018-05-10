package io.dkozak.eobaly.service

import io.dkozak.eobaly.dao.ProductDetailsRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.*
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class ProductService(
        private val productRepository: ProductRepository,
        private val productDetailsRepository: ProductDetailsRepository,
        private val parseEshopService: ParseEshopService
) {

    fun loadProductDetails(productUrl: String): ProductDetailsView {
        val product = productRepository.findByInternalName(productUrl)
                ?: throw RuntimeException("invalid internal name")
        val details = product.details.map {
            ProductDetailView(it.timestamp, parseNum(it.priceDetails), parseNum(it.amountDetails).toLong())
        }
        return ProductDetailsView(details)
    }

    fun parseProduct(url: String): Product {
        val product = parseEshopService.parseProduct(url)
        product.category = UNKNOWN_CATEGORY
        for (detail in product.details) {
            detail.product = UNKOWN_PRODUCT
            productDetailsRepository.save(detail)
        }
        return productRepository.save(product)
    }


    private fun parseNum(priceDetails: List<String>): Double =
            priceDetails.map {
                """\d+(\.\d+)?""".toRegex()
                        .find(it)
                        ?.groups
                        ?.get(0)
                        ?.value
                        ?.toDouble() ?: 0.0
            }.average()

}