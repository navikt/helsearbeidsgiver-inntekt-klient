package no.nav.helsearbeidsgiver.inntekt

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import no.nav.helsearbeidsgiver.utils.cache.LocalCache
import no.nav.helsearbeidsgiver.utils.test.mock.mockStatic
import kotlin.time.Duration

fun mockInntektKlient(
    content: String,
    status: HttpStatusCode,
): InntektKlient {
    val mockEngine = MockEngine {
        respond(
            content = content,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
        )
    }

    val mockHttpClient = HttpClient(mockEngine) { configure() }

    return mockStatic(::createHttpClient) {
        every { createHttpClient() } returns mockHttpClient
        InntektKlient("baseUrl", LocalCache.Config(entryDuration = Duration.ZERO, maxEntries = 1)) { "mock access token" }
    }
}
