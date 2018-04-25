package io.dkozak.eobaly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class EobalyParserApplication

fun main(args: Array<String>) {
    runApplication<EobalyParserApplication>(*args)
}
