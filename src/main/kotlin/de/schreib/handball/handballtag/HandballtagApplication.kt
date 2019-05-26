package de.schreib.handball.handballtag

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.filter.CommonsRequestLoggingFilter


@SpringBootApplication
class HandballtagApplication {
}

fun main(args: Array<String>) {
    runApplication<HandballtagApplication>(*args)
}
