package io.dkozak.eobaly.service


import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlPage
import io.dkozak.eobaly.domain.PriceDetails
import io.dkozak.eobaly.domain.ProductDetails
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

val MAIN_URL = "https://www.eobaly.cz"

@Service
class ParseShopService {

    fun parseMainPage(url: String = MAIN_URL): List<String> =
            Jsoup.connect(url).get().select(".mainmenu li a")
                    .map { it.attr("href") }
                    .filter { it.contains("/produkty/") }
                    .toList()

    fun parseCategoryPage(url: String): List<String> {
        var result = listOf<String>()
        val fullUrl = if (!url.startsWith(MAIN_URL)) MAIN_URL + url else url

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "error")

        val webClient = WebClient()
        webClient.options.isThrowExceptionOnScriptError = false

//        val maxPageNumber = webClient.getPage<HtmlPage>(fullUrl)
//                .querySelectorAll("a.pager-number")
//                .last()
//                .textContent
//                .toInt()
        val maxPageNumber = 1

        for (i in 1..maxPageNumber) {
            val nextUrl = "$fullUrl?page=$i"
            result += webClient.getPage<HtmlPage>(nextUrl)
                    .querySelectorAll(".product-box")
                    .map {
                        it.querySelector<DomNode>("a")
                                .attributes
                                .getNamedItem("href")
                                .nodeValue
                    }.toList()

            println(i)
            println(result)
        }
        return result
    }

    private fun parseProductBox(element: Element): String = element.select("a").attr("href")

    fun parseProductDetail(url: String): ProductDetails {
        val internalName = url.substring(url.indexOfLast { it == '/' }, url.length - 1)
                .replace(".htm", "")
        val doc = Jsoup.connect(if (!url.startsWith(MAIN_URL)) MAIN_URL + url else url).get()
        val externalName = doc.select(".col-2 h1").text()
        val productCount = doc.select(".table-spec .text-hi")
                .text()
                .replace(" ks", "")
                .replace(" ", "")
                .trim()
                .toInt()
        val priceDetails = doc.select(".table-pricing tr")
                .map { it.select("td") }
                .map { it.toList() }
                .filter { it.isNotEmpty() }
                .map {
                    val priceDetails = PriceDetails()
                    priceDetails.amount = it[0]
                            .text()
                            .replace("od", "")
                            .replace("ks", "")
                            .trim()
                            .toInt()

                    val thirdPart = it[2].text();

                    priceDetails.pricePerItem = thirdPart
                            .substring(0, thirdPart.indexOf(' '))
                            .replace(",", ".")
                            .toDouble()
                    priceDetails
                }
                .toList()

        val productDetail = ProductDetails()
        productDetail.internalName = internalName
        productDetail.externalName = externalName
        productDetail.productCount = productCount
        productDetail.priceDetails = priceDetails
        return productDetail
    }
}