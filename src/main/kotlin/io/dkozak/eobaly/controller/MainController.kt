package io.dkozak.eobaly.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController {

    @GetMapping("/info")
    fun info() = "info.html"

    @GetMapping("/")
    fun home() = "home.html"

}