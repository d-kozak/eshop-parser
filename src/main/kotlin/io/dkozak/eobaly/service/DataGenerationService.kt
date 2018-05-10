package io.dkozak.eobaly.service

import io.dkozak.eobaly.dao.ProductDetailsRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.ProductDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional


@Service
@Transactional
class DataGenerationService(
        private val productRepository: ProductRepository,
        private val productDetailsRepository: ProductDetailsRepository
) {

    fun generatePriceForAll() {
        val random = Random()
        productRepository.findAll().map {
            val oldedestDetails = if (it.details.size > 0) it.details[0] else null
            var price = if (oldedestDetails != null) parseNum(oldedestDetails.priceDetails) else random.nextInt() * 200.0
            val amount = if (oldedestDetails != null) parseNum(oldedestDetails.amountDetails) else random.nextInt() * 20.0
            var currentDay = if (oldedestDetails != null) oldedestDetails.timestamp else Date()
            for (i in 1..10) {
                val deviation = 0.1 * price
                price = if (random.nextBoolean()) price + deviation else price - deviation
                val productDetails = ProductDetails()
                productDetails.product = it
                currentDay = removeOneDay(currentDay, productDetails)
                productDetails.timestamp = currentDay
                productDetails.amountDetails = mutableListOf(amount.toString())
                productDetails.priceDetails = mutableListOf(price.toString())
                it.details.add(productDetailsRepository.save(productDetails))
                productRepository.save(it)
            }
            it
        }.forEach {
            productRepository.save(it)
        }
    }

    private fun removeOneDay(currentDay: Date, productDetails: ProductDetails): Date {
        val cal = Calendar.getInstance()
        cal.time = currentDay
        cal.add(Calendar.DATE, -1)
        return cal.time

    }

}
