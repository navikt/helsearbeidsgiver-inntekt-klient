package no.nav.helsearbeidsgiver.inntekt

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlin.reflect.KFunction

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

    return mockFn(::createHttpClient) {
        every { createHttpClient() } returns mockHttpClient
        InntektKlient("url") { "fake token" }
    }
}

private fun <T> mockFn(fn: KFunction<*>, block: () -> T): T {
    mockkStatic(fn)
    return try {
        block()
    } finally {
        unmockkStatic(fn)
    }
}
