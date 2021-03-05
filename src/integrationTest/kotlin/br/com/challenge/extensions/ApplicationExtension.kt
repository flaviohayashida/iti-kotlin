package br.com.challenge.extensions

import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.DefaultHttpClient
import io.micronaut.http.client.DefaultHttpClientConfiguration
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class ApplicationExtension: BeforeAllCallback, AfterAllCallback {

    private lateinit var embeddedServer: EmbeddedServer
    private lateinit var httpClient: HttpClient

    override fun beforeAll(context: ExtensionContext?) {
        embeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
    }

    override fun afterAll(context: ExtensionContext?) {
        if(this::httpClient.isInitialized) {
            httpClient.close()
        }
        embeddedServer.close()
    }

    fun client(): HttpClient {
        if(!this::httpClient.isInitialized) {
            httpClient = DefaultHttpClient(embeddedServer.url, DefaultHttpClientConfiguration().apply {
                isExceptionOnErrorStatus = false
            })
        }

        return httpClient
    }
}


