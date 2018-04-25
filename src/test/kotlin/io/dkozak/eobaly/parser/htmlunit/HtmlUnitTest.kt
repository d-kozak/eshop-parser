package io.dkozak.eobaly.parser.htmlunit

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.junit.Test

class HtmlUnitTest {

    @Test
    fun `html unit`() {
        val webClient = WebClient()
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.use {
            val page: HtmlPage = webClient.getPage("https://www.eobaly.cz/produkty/kartonove-krabice.htm?page=2")

            val second = page.querySelectorAll(".product-box")
                    .map {
                        val a = it.querySelector<DomNode>("a")
                        a.attributes.getNamedItem("href").nodeValue
                    }
                    .toList()

            val links = page.querySelectorAll("a.pager-number")
            println(links.last().textContent)

            val page2: HtmlPage = webClient.getPage("https://www.eobaly.cz/produkty/kartonove-krabice.htm?page=24")

            val third = page2.querySelectorAll(".product-box")
                    .toList()


            println("done")
            print(second)
            print(third)
        }
    }


}