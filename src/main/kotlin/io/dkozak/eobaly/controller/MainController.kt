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

@Controller
class MainController(
        private val productCategoryRepository: ProductCategoryRepository,
        private val productRepository: ProductRepository,
        private val errorLogRepository: ErrorLogRepository,
        private val eobalyParsingTask: EobalyParsingTask
) {


    @GetMapping("/info")
    fun info() = "info.html"

    @GetMapping("/")
    fun home(model: Model): String {
        model["products"] = productRepository.findAll()
        model["productCategories"] = productCategoryRepository.findAll()
        return "home.html"
    }

    @GetMapping("/category/{category}")
    fun getInCategory(model: Model, @PathVariable category: String): String {
        val category = productCategoryRepository.findByName(category)
        if (category == null) {
            throw RuntimeException("wrong category")
        }
        model["products"] = productRepository.findByCategory(category)
        model["productCategories"] = productCategoryRepository.findAll()

        return "home.html"
    }

    @GetMapping("/product/{internalName}")
    fun product(model: Model, @PathVariable internalName: String): String {
        val product = productRepository.findByInternalName(internalName)
        if (product == null) {
            throw RuntimeException("product not found")
        }
        model["product"] = product
        model["productCategories"] = productCategoryRepository.findAll()
        return "product.html"
    }

    @GetMapping("/error-log")
    fun errorLog(model: Model): String {
        model["errorLogs"] = errorLogRepository.findAll()
        return "error-log.html"
    }

    @GetMapping("/api/parse/{internalName}")
    fun startParsing(@PathVariable(required = false) internalName: String?): String {
        if (internalName == null) {
            eobalyParsingTask.start()
        } else {
            eobalyParsingTask.startFor(internalName)
        }
        return "redirect:/"
    }

}