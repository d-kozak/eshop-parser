package io.dkozak.eobaly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EobalyParserApplication

fun main(args: Array<String>) {
    runApplication<EobalyParserApplication>(*args)
}
