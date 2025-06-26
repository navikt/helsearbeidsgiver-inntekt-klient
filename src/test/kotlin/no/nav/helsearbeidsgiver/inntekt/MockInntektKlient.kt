package no.nav.helsearbeidsgiver.inntekt

import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.nulls.shouldNotBeNull
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import no.nav.helsearbeidsgiver.utils.cache.LocalCache
import no.nav.helsearbeidsgiver.utils.test.mock.mockStatic
import kotlin.time.Duration

@OptIn(ExperimentalStdlibApi::class, ExperimentalCoroutinesApi::class)
fun mockInntektKlient(vararg responses: Pair<HttpStatusCode, String>): InntektKlient {
    val mockEngine = MockEngine.create {
        reuseHandlers = false
        requestHandlers.addAll(
            responses.map { (status, content) ->
                {
                    if (content == "timeout") {
                        // Skrur den virtuelle klokka fremover, nok til at timeout forårsakes
                        dispatcher.shouldNotBeNull().testCoroutineScheduler.advanceTimeBy(1)
                    }
                    respond(
                        content = content,
                        status = status,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            },
        )
    }

    val mockHttpClient = HttpClient(mockEngine) { configure() }

    return mockStatic(::createHttpClient) {
        every { createHttpClient() } returns mockHttpClient
        InntektKlient("baseUrl", LocalCache.Config(entryDuration = Duration.ZERO, maxEntries = 1)) { "mock access token" }
    }
}
