package io.dkozak.eobaly.controller

import io.dkozak.eobaly.dao.ErrorLogRepository
import io.dkozak.eobaly.dao.ProductCategoryRepository
import io.dkozak.eobaly.dao.ProductRepository
import io.dkozak.eobaly.tasks.EobalyParsingTask
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class MainController(
        private val productCategoryRepository: ProductCategoryRepository,
        private val productRepository: ProductRepository,
        private val errorLogRepository: ErrorLogRepository,
        private val eobalyParsingTask: EobalyParsingTask
) {


    @GetMapping("/info")
    fun info() = "about.html"

    @GetMapping("/")
    fun home(model: Model, @RequestParam(required = false) search: String?): String {
        model["products"] = if (search != null) productRepository.findByExternalNameLike("%$search%") else productRepository.findAll()
        model["productCategories"] = productCategoryRepository.findAll()
        return "home.html"
    }

    @GetMapping("/category/{category}")
    fun getInCategory(model: Model, @PathVariable category: String, @RequestParam(required = false) search: String?): String {
        val productCategory = productCategoryRepository.findByName(category) ?: throw RuntimeException("wrong category")
        model["products"] = if (search != null) productRepository.findByCategoryAndExternalNameLike(productCategory, "%$search%") else productRepository.findByCategory(productCategory)
        model["productCategories"] = productCategoryRepository.findAll()
        model["categoryName"] = category
        return "home.html"
    }

    @GetMapping("/product/{internalName}")
    fun product(model: Model, @PathVariable internalName: String): String {
        val product = productRepository.findByInternalName(internalName) ?: throw RuntimeException("product not found")
        model["product"] = product
        model["productCategories"] = productCategoryRepository.findAll()
        return "product.html"
    }

    @GetMapping("/error-log")
    fun errorLog(model: Model): String {
        model["errorLogs"] = errorLogRepository.findAll()
        model["productCategories"] = productCategoryRepository.findAll()
        return "error-log.html"
    }

    @GetMapping("/about")
    fun about(model: Model): String {
        model["productCategories"] = productCategoryRepository.findAll()
        return "about.html"
    }
}