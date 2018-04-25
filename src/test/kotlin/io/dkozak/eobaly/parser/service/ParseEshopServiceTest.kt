package io.dkozak.eobaly.parser.service

import io.dkozak.eobaly.service.ParseShopService
import io.dkozak.eobaly.tasks.ParsingTask
import org.jsoup.Jsoup
import org.junit.Before
import org.junit.Test

class ParseEshopServiceTest {
    @Test
    fun `just parsing test`() {
        val doc = Jsoup.connect("https://www.eobaly.cz/produkty/kartonove-krabice.htm?page=2").get()
        val select = doc.select("a.pager-number")
        println(select.last().text())
    }

    private lateinit var parsingTask: ParsingTask

    @Before
    fun init() {
        val parseEshopService = ParseShopService()
        parsingTask = ParsingTask(parseEshopService)
    }

    @Test
    fun startParseTaskTest() {
        parsingTask.start()
    }
}