package io.dkozak.eobaly.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthenticationController {

    @GetMapping("/login")
    fun login() = "login.html"

    @GetMapping("/login-error")
    fun loginError(model: Model): String {
        model["loginError"] = true
        return "login.html"
    }

    @GetMapping("/login-logout")
    fun logout(model: Model): String {
        model["logout"] = true
        return "login.html"
    }


}