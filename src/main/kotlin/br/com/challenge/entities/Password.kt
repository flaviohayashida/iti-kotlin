package br.com.challenge.entities

typealias PasswordStatus = Pair<Boolean, List<String>>

// Entities for controller communication
data class PasswordValidationRequest(
    val password: String
)

data class PasswordValidationResponse(
    val isValid: Boolean,
    val validationErrors: List<String>
)

data class PasswordValidationErrorResponse(
    val message: String
)
