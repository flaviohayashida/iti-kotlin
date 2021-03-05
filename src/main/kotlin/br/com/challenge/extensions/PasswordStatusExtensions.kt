package br.com.challenge.extensions

import br.com.challenge.entities.PasswordStatus
import br.com.challenge.entities.PasswordValidationResponse

fun PasswordStatus.toPasswordResponse(): PasswordValidationResponse = PasswordValidationResponse(
    isValid = first,
    validationErrors = second
)
