package no.nav.helsearbeidsgiver.inntekt

import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.utils.json.fromJson
import no.nav.helsearbeidsgiver.utils.json.toJson
import java.time.YearMonth

class InntektKlient(
    private val baseUrl: String,
    private val getAccessToken: () -> String,
) {
    private val httpClient = createHttpClient()

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

        val response = httpClient.post("$baseUrl/api/v1/hentinntektliste") {
            contentType(ContentType.Application.Json)
            bearerAuth(getAccessToken())
            header("Nav-Consumer-Id", navConsumerId)
            header("Nav-Call-Id", callId)

            setBody(request)
        }
            .bodyAsText()
            .fromJson(InntektResponse.serializer())

        return response.tilInntektPerOrgnrOgMaaned()
    }
}

private fun String.tilIdent(): Ident =
    Ident(this, "NATURLIG_IDENT")
