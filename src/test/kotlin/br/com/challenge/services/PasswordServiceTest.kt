package br.com.challenge.services

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PasswordServiceTest {

    private val passwordService: PasswordService = PasswordService()

    @ParameterizedTest
    @ValueSource(strings = ["AbTp9!foo", "Jhdg*hsh1", "!Jhdgahsh1", "Jhdgahsh1!"])
    fun `should run successfully with a valid password`(password: String) {
        val (isValid, validationErrors) = passwordService.validatePassword(password)

        assertThat(isValid).isTrue()
        assertThat(validationErrors).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["AbTp9!fo", "Kt1!"])
    fun `password must be invalid if length is smaller than 9`(password: String) {
        val (isValid, validationErrors) = passwordService.validatePassword(password)

        assertThat(isValid).isFalse()
        assertThat(validationErrors.isEmpty()).isFalse()
        assertThat(validationErrors).contains(PASSWORD_LENGTH_ERROR_MESSAGE)
    }

    @ParameterizedTest
    @ValueSource(strings = ["abtp9!foo", "ABTP9!FOO"])
    fun `password must be invalid if it doesn't have at least 1 capital letter and 1 lower case letter`(password: String) {
        val (isValid, validationErrors) = passwordService.validatePassword(password)

        assertThat(isValid).isFalse()
        assertThat(validationErrors.isEmpty()).isFalse()
        assertThat(validationErrors).contains(PASSWORD_LETTER_CASE_ERROR_MESSAGE)
    }

    @ParameterizedTest
    @ValueSource(strings = ["AbTp(!foo", "Jhdg*hsh!"])
    fun `password must be invalid if it doesn't have at least 1 digit`(password: String) {
        val (isValid, validationErrors) = passwordService.validatePassword(password)

        assertThat(isValid).isFalse()
        assertThat(validationErrors.isEmpty()).isFalse()
        assertThat(validationErrors).contains(PASSWORD_NO_DIGIT_ERROR_MESSAGE)
    }

    @ParameterizedTest
    @ValueSource(strings = ["AbTp91foo", "Jhdg8hsh1"])
    fun `password must be invalid if it doesn't have any special character`(password: String) {
        val (isValid, validationErrors) = passwordService.validatePassword(password)

        assertThat(isValid).isFalse()
        assertThat(validationErrors.isEmpty()).isFalse()
        assertThat(validationErrors).contains(PASSWORD_NO_SPECIAL_CHAR_ERROR_MESSAGE)
    }

    companion object {
        private const val PASSWORD_LENGTH_ERROR_MESSAGE = "Password must have at least 9 characters"
        private const val PASSWORD_LETTER_CASE_ERROR_MESSAGE = "Password must have at least one lower and one upper case letters"
        private const val PASSWORD_NO_DIGIT_ERROR_MESSAGE = "Password must have at least one digit"
        private const val PASSWORD_NO_SPECIAL_CHAR_ERROR_MESSAGE = "Password must have at least one special char"
    }
}
