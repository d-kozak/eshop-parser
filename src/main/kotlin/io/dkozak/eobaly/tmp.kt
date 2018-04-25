package io.dkozak.eobaly

import java.io.File

fun loadFileContent(path: String) = File(path).readText()

fun parseMainPage(html: String): List<String> =
        Jsoup.parse(html).select(".mainmenu li a")
                .map { it.attr("href") }
                .filter { it.contains("/produkty/") }
                .map { it.replace(".htm", "") }
                .map { it.replace("https://www.eobaly.cz/produkty/", "") }
                .toList()


fun parseProductBox(element: Element): String = element.select("a").attr("href")


fun parseCategoryPage(html: String?): List<String> {
    var result = listOf<String>()
    var currentPage = html
    while (currentPage != null) {
        val doc = Jsoup.parse(currentPage)

        result += doc.select(".product-box")
                .map { parseProductBox(it) }
                .toList()
        val nextPageLink = doc.select("a.right")
        currentPage = if (nextPageLink.size != 0)
            nextPageLink.attr("href")
        else
            null

    }
    return result;
}

data class ProductDetails(val internalName: String, val externalName: String, val productCount: Int, val priceDetails: List<Pair<String, String>>)

fun parseProductDetail(html: String): ProductDetails {
    val internalName = html.substring(html.indexOfLast { it == '/' }, html.length - 1)
            .replace(".htm", "")
    val doc = Jsoup.parse(html)
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
            .map { it[0].text() to it[2].text() }
            .toList()

    return ProductDetails(internalName, externalName, productCount, priceDetails)

}

fun main(args: Array<String>) {
//    val html = loadFileContent("stranky/main.html")
//    parseMainPage(html)

//    val html = loadFileContent("stranky/category.html")
//    parseCategoryPage(html)
    val html = loadFileContent("stranky/item.html")
    parseProductDetail(html)
}
