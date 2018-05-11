package io.dkozak.eobaly.service

import io.dkozak.eobaly.dao.ProductCategoryRepository
import io.dkozak.eobaly.dao.ProductDetailsRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.Product
import io.dkozak.eobaly.domain.ProductDetailView
import io.dkozak.eobaly.domain.ProductDetailsView
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class ProductService(
        private val productRepository: ProductRepository,
        private val productCategoryRepository: ProductCategoryRepository,
        private val productDetailsRepository: ProductDetailsRepository,
        private val parseEshopService: ParseEshopService
) {

    fun loadProductDetails(productUrl: String): ProductDetailsView {
        val product = productRepository.findByInternalName(productUrl)
                ?: throw RuntimeException("invalid internal name")
        val details = product.details.map {
            ProductDetailView(it.timestamp, parseNum(it.priceDetails), parseNum(it.productCount).toLong())
        }
        return ProductDetailsView(details)
    }

    fun parseProduct(url: String): Product {
        val (product, categoryUrl) = parseEshopService.parseProduct(url)
        val productInDtb = productRepository.findByInternalName(product.internalName)
        if (productInDtb == null) {
            product.category = parseEshopService.getProductCategory(categoryUrl)
        }
        for (detail in product.details) {
            detail.product = if (productInDtb != null) productInDtb else product
            productDetailsRepository.save(detail)
        }
        return productRepository.save(if (productInDtb != null) productInDtb else product)
    }
}

fun parseNum(priceDetails: List<String>): Double =
        priceDetails.map {
            parseNum(it)
        }.average()

fun parseNum(it: String): Double {
    return """\d+(\.\d+)?""".toRegex()
            .find(it)
            ?.groups
            ?.get(0)
            ?.value
            ?.toDouble() ?: 0.0
}