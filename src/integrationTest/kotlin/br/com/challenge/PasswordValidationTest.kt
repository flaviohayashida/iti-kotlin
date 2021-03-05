package br.com.challenge

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNullOrEmpty
import assertk.assertions.isTrue
import br.com.challenge.entities.PasswordValidationErrorResponse
import br.com.challenge.entities.PasswordValidationRequest
import br.com.challenge.entities.PasswordValidationResponse
import br.com.challenge.extensions.ApplicationExtension
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PasswordValidationTest {

    @JvmField
    @RegisterExtension
    val application = ApplicationExtension()

    @Test
    fun `return a 200 http status response calling password validation with a valid password`() {
        val httpResponse = createPasswordValidationRequest(VALID_PASSWORD)

        assertThat(httpResponse.status).isEqualTo(HttpStatus.OK)
        assertThat(httpResponse.body()).isNotNull().given { passwordResponse ->
            assertThat(passwordResponse.isValid).isTrue()
            assertThat(passwordResponse.validationErrors).isNullOrEmpty()
        }
    }

    @Test
    fun `return a 400 http status response calling password validation with a password with only one validation issue`() {
        val httpResponse = createPasswordValidationRequest(TOO_SMALL_PASSWORD)

        assertThat(httpResponse.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(httpResponse.body).isNotNull().given { passwordResponseOptional ->
            assertThat(passwordResponseOptional.isPresent).isTrue()

            val passwordResponse = passwordResponseOptional.get()

            assertThat(passwordResponse.isValid).isFalse()
            assertThat(passwordResponse.validationErrors).isNotNull().given { errors ->
                assertThat(errors).all {
                    hasSize(1)
                    contains(PASSWORD_LENGTH_ERROR_MESSAGE)
                }
            }
        }
    }

    @Test
    fun `return a 400 http status response calling password validation with a password with multiple validation issues`() {
        val httpResponse = createPasswordValidationRequest(MULTIPLE_ISSUES_PASSWORD)

        assertThat(httpResponse.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(httpResponse.body).isNotNull().given { passwordResponseOptional ->
            assertThat(passwordResponseOptional.isPresent).isTrue()

            val passwordResponse = passwordResponseOptional.get()

            assertThat(passwordResponse.isValid).isFalse()
            assertThat(passwordResponse.validationErrors).isNotNull().given { errors ->
                assertThat(errors).all {
                    hasSize(4)
                    contains(PASSWORD_LENGTH_ERROR_MESSAGE)
                    contains(PASSWORD_LETTER_CASE_ERROR_MESSAGE)
                    contains(PASSWORD_NO_DIGIT_ERROR_MESSAGE)
                    contains(PASSWORD_NO_SPECIAL_CHAR_ERROR_MESSAGE)
                }
            }
        }
    }

    @Test
    fun `return a 500 http status response when the application couldn't parse the payload`() {
        data class InvalidPayload(val invalid: String)

        val request = HttpRequest.POST(PASSWORD_VALIDATION_ENDPOINT, InvalidPayload(VALID_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON)

        runCatching { application.client().toBlocking().exchange(request, Argument.of(String::class.java)) }.onFailure { exception ->
            exception as HttpClientResponseException

            assertThat(exception.status).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(exception.response.body()).isNotNull().given { responseContent ->
                jacksonObjectMapper().readValue(responseContent.toString(), PasswordValidationErrorResponse::class.java).let { errorResponse ->
                    assertThat(errorResponse.message).isEqualTo(PASSWORD_VALIDATION_SERVICE_PAYLOAD_ERROR)
                }
            }
        }
    }

    private fun createPasswordValidationRequest(password: String): HttpResponse<PasswordValidationResponse> {
        val request = HttpRequest.POST(PASSWORD_VALIDATION_ENDPOINT, PasswordValidationRequest(password))
            .contentType(MediaType.APPLICATION_JSON)

        val argumentType = Argument.of(PasswordValidationResponse::class.java)

        return application.client().toBlocking().exchange(request, argumentType, argumentType)
    }

    companion object {
        private const val PASSWORD_VALIDATION_ENDPOINT = "/"
        private const val VALID_PASSWORD = "AbTp9!foo"
        private const val TOO_SMALL_PASSWORD = "AbTp9!fo"
        private const val MULTIPLE_ISSUES_PASSWORD = "test"
        private const val PASSWORD_LENGTH_ERROR_MESSAGE = "Password must have at least 9 characters"
        private const val PASSWORD_LETTER_CASE_ERROR_MESSAGE = "Password must have at least one lower and one upper case letters"
        private const val PASSWORD_NO_DIGIT_ERROR_MESSAGE = "Password must have at least one digit"
        private const val PASSWORD_NO_SPECIAL_CHAR_ERROR_MESSAGE = "Password must have at least one special char"
        private const val PASSWORD_VALIDATION_SERVICE_PAYLOAD_ERROR = "was impossible to serialize the payload [Required argument [PasswordValidationRequest passwordValidationRequest] not specified]"
    }
}
