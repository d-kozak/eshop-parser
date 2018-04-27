package io.dkozak.eobaly.parser.service

import io.dkozak.eobaly.dao.ProductCategoryRepository
import io.dkozak.eobaly.dao.ProductDetailsRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.service.ParseShopService
import io.dkozak.eobaly.tasks.EobalyParsingTask
import org.jsoup.Jsoup
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ParseEshopServiceTest {

    @Test
    fun `just parsing test`() {
        val doc = Jsoup.connect("https://www.eobaly.cz/produkty/kartonove-krabice.htm?page=2").get()
        val select = doc.select("a.pager-number")
        println(select.last().text())
    }

    private lateinit var eobalyParsingTask: EobalyParsingTask

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var productCategoryRepository: ProductCategoryRepository

    @Mock
    lateinit var productDetailsRepository: ProductDetailsRepository

    @Before
    fun init() {
        val parseEshopService = ParseShopService()
        eobalyParsingTask = EobalyParsingTask(parseEshopService, productRepository, productDetailsRepository, productCategoryRepository)
    }

    @Test
    fun startParseTaskTest() {
        eobalyParsingTask.start()
    }
}