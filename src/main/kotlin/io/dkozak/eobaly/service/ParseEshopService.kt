package io.dkozak.eobaly.service


import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlPage
import io.dkozak.eobaly.dao.ProductCategoryRepository
import io.dkozak.eobaly.dao.ProductDetailsRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.domain.Product
import io.dkozak.eobaly.domain.ProductCategory
import io.dkozak.eobaly.domain.ProductDetails
import io.dkozak.eobaly.tasks.EobalyParsingTask
import org.jboss.logging.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service
import java.util.logging.Level
import javax.transaction.Transactional


val MAIN_URL = "https://www.eobaly.cz"

@Service
@Transactional
class ParseShopService(
        private val productRepository: ProductRepository,
        private val productDetailsRepository: ProductDetailsRepository,
        private val productCategoryRepository: ProductCategoryRepository
) {

    private val log = Logger.getLogger(EobalyParsingTask::class.java)

    fun parseMainPage(url: String = MAIN_URL): List<String> =
            Jsoup.connect(url).get().select(".mainmenu li a")
                    .map { it.attr("href") }
                    .filter { it.contains("/produkty/") }
                    .toList()

    fun parseCategoryPage(url: String, productRepository: ProductRepository): Pair<List<String>, List<String>> {
        val result = mutableListOf<String>()
        val failedLinks = mutableListOf<String>()
        val fullUrl = if (!url.startsWith(MAIN_URL)) MAIN_URL + url else url

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "error")
        java.util.logging.Logger.getLogger("com.gargoylesoftware").level = Level.OFF
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        val webClient = WebClient()
        webClient.options.isThrowExceptionOnScriptError = false

        webClient.use {
            val maxPageNumber = webClient.getPage<HtmlPage>(fullUrl)
                    .querySelectorAll("a.pager-number")
                    .last()
                    .textContent
                    .toInt()

            for (i in 1..maxPageNumber) {
                val nextUrl = "$fullUrl?page=$i"
                try {
                    result.addAll(webClient.getPage<HtmlPage>(nextUrl)
                            .querySelectorAll(".product-box")
                            .map {
                                it.querySelector<DomNode>("a")
                                        .attributes
                                        .getNamedItem("href")
                                        .nodeValue
                            }.toList())

                } catch (ex: Exception) {
                    failedLinks.add(nextUrl)
                    ex.printStackTrace()
                    log.warn("Could not parse link $nextUrl, because ${ex.message}")
                }
            }
            return result to failedLinks
        }
    }

    private fun parseProductBox(element: Element): String = element.select("a").attr("href")

    fun parseProduct(url: String): Product {
        val internalName = parseNameFromUrl(url)
        val doc = Jsoup.connect(if (!url.startsWith(MAIN_URL)) MAIN_URL + url else url).get()
        val externalName = doc.select(".col-2 h1").text()
        val productCount = doc.select(".table-spec .text-hi")
                .text()
                .replace(" ks", "")
                .replace(" ", "")
                .trim()
        val priceDetails = doc.select(".table-pricing tr")
                .map { it.select("td") }
                .map { it.toList() }
                .filter { it.isNotEmpty() }
                .map {
                    val amount = it[0]
                            .text()
                    val thirdPart = it[2].text()
                    val pricePerItem = thirdPart
                            .substring(0, thirdPart.indexOf(' '))
                            .replace(",", ".")
                    amount to pricePerItem
                }
                .toMutableList()

        val imgUrl = doc.select(".thickbox img")
                .attr("src")

        val productDetails = ProductDetails()
        productDetails.productCount = productCount
        productDetails.amountDetails = mutableListOf(priceDetails[0].first)
        productDetails.priceDetails = mutableListOf(priceDetails[0].second)
        val product = Product()
        product.details = mutableListOf(productDetails)
        product.internalName = internalName
        product.externalName = externalName
        product.url = url
        product.imgUrl = imgUrl
        return product
    }

    fun parseProduct(url: String, productCategory: ProductCategory) {
        val parsedProduct = parseProduct(url)
        var productInDb = productRepository.findByInternalName(parsedProduct.internalName)
        if (productInDb == null) {
            parsedProduct.category = productCategory
            val detailsInDb = productDetailsRepository.save(parsedProduct.details[0])
            productInDb = productRepository.save(parsedProduct)
            detailsInDb.product = productInDb
            parsedProduct.details.add(detailsInDb)
        } else {
            var newProductDetails = parsedProduct.details[0]
            newProductDetails = productDetailsRepository.save(newProductDetails)
            productInDb.details.add(newProductDetails)
            newProductDetails.product = productInDb
            productInDb.details.add(newProductDetails)
        }
    }

    fun getProductCategory(categoryUrl: String): ProductCategory {
        var productCategory = productCategoryRepository.findByUrl(categoryUrl)
        if (productCategory == null) {
            productCategory = ProductCategory()
            productCategory.name = parseNameFromUrl(categoryUrl)
            productCategory.url = categoryUrl
            productCategoryRepository.save(productCategory)
        }
        return productCategory
    }
}

fun parseNameFromUrl(categoryUrl: String): String {
    val slashIndex = categoryUrl.lastIndexOf("/")
    if (slashIndex == -1)
        throw RuntimeException("Invalid string")
    val dotIndex = categoryUrl.lastIndexOf(".")
    if (dotIndex == -1) {
        throw RuntimeException("Invalid string")
    }
    return categoryUrl.substring(slashIndex + 1, dotIndex)
}