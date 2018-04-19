package io.dkozak.eobaly.controller

import io.dkozak.eobaly.dao.ProductCategoryRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController(private val productCategoryRepository: ProductCategoryRepository) {



    @GetMapping("/info")
    fun info() = "info.html"

    @GetMapping("/")
    fun home(model: Model): String {
        model["productCategories"] = productCategoryRepository.findAll()
        return "home.html"
    }

}