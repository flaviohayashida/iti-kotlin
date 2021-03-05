package br.com.challenge.services

import br.com.challenge.entities.PasswordStatus

class PasswordService {

    fun validatePassword(password: String): PasswordStatus {

        //TODO: Implement your code here

        return PasswordStatus(
            false,
            emptyList()
        )
    }

    companion object {
        private const val PASSWORD_MINIMUM_SIZE = 9
        private const val PASSWORD_LENGTH_ERROR_MESSAGE = "Password must have at least 9 characters"
        private const val PASSWORD_LETTER_CASE_ERROR_MESSAGE = "Password must have at least one lower and one upper case letters"
        private const val PASSWORD_NO_DIGIT_ERROR_MESSAGE = "Password must have at least one digit"
        private const val PASSWORD_NO_SPECIAL_CHAR_ERROR_MESSAGE = "Password must have at least one special char"
    }
}
