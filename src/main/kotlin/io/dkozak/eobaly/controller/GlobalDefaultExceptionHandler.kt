package io.dkozak.eobaly.controller

import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class GlobalDefaultExceptionHandler {


    @ExceptionHandler(value = [(Exception::class)])
    fun error(model: Model, request: HttpServletRequest, exception: Exception): String {

        model["exception"] = exception
        model["url"] = request.requestURL
        return "error.html"
    }
}

