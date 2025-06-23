package no.nav.helsearbeidsgiver.inntekt

import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.utils.cache.LocalCache
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJson
import java.time.YearMonth

class InntektKlient(
    baseUrl: String,
    cacheConfig: LocalCache.Config,
    private val getAccessToken: () -> String,
) {
    private val url = "$baseUrl/api/v1/hentinntektliste"
    private val httpClient = createHttpClient()
    private val cache = LocalCache<InntektResponse>(cacheConfig)

    suspend fun hentInntektPerOrgnrOgMaaned(
        fnr: String,
        fom: YearMonth,
        tom: YearMonth,
        navConsumerId: String,
        callId: String,
    ): Map<String, Map<YearMonth, Double>> {
        val request = InntektRequest(
            ident = fnr.tilIdent(),
            maanedFom = fom,
            maanedTom = tom,
        )
            .toJson(InntektRequest.serializer())

        val response =
            cache.getOrPut(request.toString()) {
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    bearerAuth(getAccessToken())
                    header("Nav-Consumer-Id", navConsumerId)
                    header("Nav-Call-Id", callId)

                    setBody(request)
                }
                    .bodyAsText()
                    .fromJson(InntektResponse.serializer())
            }

        return response.tilInntektPerOrgnrOgMaaned()
    }
}

private fun String.tilIdent(): Ident =
    Ident(this, "NATURLIG_IDENT")
