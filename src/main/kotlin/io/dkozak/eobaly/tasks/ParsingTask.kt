package io.dkozak.eobaly.tasks

import io.dkozak.eobaly.service.ParseShopService
import org.jboss.logging.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ParsingTask(private val parseEshopService: ParseShopService) {

    private val log = Logger.getLogger(ParsingTask::class.java)

    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 60)
    fun start() {
        log.info("Starting")
        val categories = parseEshopService.parseMainPage()
        for (category in categories) {
            val products = parseEshopService.parseCategoryPage(category)
            for (product in products) {
                val productDetail = parseEshopService.parseProductDetail(product)
                println(productDetail)
            }
        }

    }
}