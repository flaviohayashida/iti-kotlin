package br.com.challenge

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Password validation service",
        version = "1.0.0",
        description = "password validation endpoint definitions",
        contact = Contact(url = "http://password-validation.com.br", name = "backend-challenge")
    )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("br.com.challenge")
                .start()
    }
}
