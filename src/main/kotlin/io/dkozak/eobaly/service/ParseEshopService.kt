package io.dkozak.eobaly.service


import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.DomNode
import com.gargoylesoftware.htmlunit.html.HtmlPage
import io.dkozak.eobaly.dao.*
import io.dkozak.eobaly.domain.*
import io.dkozak.eobaly.tasks.EobalyParsingTask
import io.dkozak.eobaly.utils.stackTraceAsString
import org.jboss.logging.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service
import java.util.logging.Level
import javax.transaction.Transactional


val MAIN_URL = "https://www.eobaly.cz"

@Service
@Transactional
class ParseEshopService(
        private val productRepository: ProductRepository,
        private val productDetailsRepository: ProductDetailsRepository,
        private val productCategoryRepository: ProductCategoryRepository,
        private val errorLogRepository: ErrorLogRepository,
        private val productLogRepository: ProductLogRepository
) {

    private val log = Logger.getLogger(EobalyParsingTask::class.java)

    fun parseMainPage(url: String = MAIN_URL): List<String> =
            Jsoup.connect(url).get().select(".mainmenu li a")
                    .map { it.attr("href") }
                    .filter { it.contains("/produkty/") }
                    .toList()

    fun parseCategoryPage(url: String, productRepository: ProductRepository, executionId: Long): List<String> {
        val result = mutableListOf<String>()
        val fullUrl = if (!url.startsWith(MAIN_URL)) MAIN_URL + url else url

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "error")
        java.util.logging.Logger.getLogger("com.gargoylesoftware").level = Level.OFF
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
        val webClient = WebClient()
        webClient.options.isThrowExceptionOnScriptError = false

        webClient.use {
            val nodeList = webClient.getPage<HtmlPage>(fullUrl)
                    .querySelectorAll("a.pager-number")
            val maxPageNumber = if (nodeList.size > 0) {
                nodeList.last()
                        .textContent
                        .toInt()
            } else 1
            productLogRepository.save(ProductLog(url = fullUrl, state = "SUCCESS", executionId = executionId))
            productLogRepository.flush()
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
                    productLogRepository.save(ProductLog(url = nextUrl, state = "SUCCESS", executionId = executionId))
                    productLogRepository.flush()

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    log.warn("Could not parse link $nextUrl, because ${ex.message}")
                    val log = ErrorLog()
                    log.type = "PRODUCT_PAGE_FAIL"
                    log.message = ex.message ?: ""
                    log.url = nextUrl
                    log.stackTrace = ex.stackTraceAsString()
                    errorLogRepository.save(log)
                    productLogRepository.save(ProductLog(url = nextUrl, state = "FAILURE", executionId = executionId))
                    productLogRepository.flush()
                }
            }
            return result
        }
    }

    private fun parseProductBox(element: Element): String = element.select("a").attr("href")

    fun parseProduct(url: String): Pair<Product, String> {
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

        val categoryUrl = doc.select(".mainmenu .sel a").attr("href")

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
        return product to categoryUrl
    }

    fun parseProduct(url: String, productCategory: ProductCategory): Product {
        val (parsedProduct, _) = parseProduct(url)
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
        return productInDb
    }

    fun getProductCategory(categoryUrl: String, executionId: Long = -1): ProductCategory {
        var productCategory = productCategoryRepository.findByUrl(categoryUrl)
        if (productCategory == null) {
            productCategory = ProductCategory()
            productCategory.name = parseNameFromUrl(categoryUrl)
            productCategory.url = categoryUrl
            productCategoryRepository.save(productCategory)
            productLogRepository.save(ProductLog(url = categoryUrl, state = "SUCCESS", executionId = executionId))
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