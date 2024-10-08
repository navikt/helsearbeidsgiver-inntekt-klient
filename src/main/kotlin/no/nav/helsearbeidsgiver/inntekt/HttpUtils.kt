package no.nav.helsearbeidsgiver.inntekt

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import no.nav.helsearbeidsgiver.utils.json.jsonConfig

internal fun createHttpClient(): HttpClient =
    HttpClient(Apache5) { configure() }

internal fun HttpClientConfig<*>.configure() {
    expectSuccess = true

    install(ContentNegotiation) {
        json(jsonConfig)
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 5000
        connectTimeoutMillis = 2000
        socketTimeoutMillis = 2000
    }
}
