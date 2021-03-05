package br.com.challenge.controllers

import br.com.challenge.entities.PasswordValidationRequest
import br.com.challenge.entities.PasswordValidationResponse
import br.com.challenge.extensions.toPasswordResponse
import br.com.challenge.services.PasswordService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces

@Controller("/")
class PasswordController(
    private val passwordService: PasswordService
) {

    @Post
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun validatePassword(passwordValidationRequest: PasswordValidationRequest): HttpResponse<PasswordValidationResponse> =
        passwordService.validatePassword(passwordValidationRequest.password).toPasswordResponse().let { passwordValidationResponse ->
            if(passwordValidationResponse.isValid) HttpResponse.ok(passwordValidationResponse)
            else HttpResponse.badRequest(passwordValidationResponse)
        }
}
