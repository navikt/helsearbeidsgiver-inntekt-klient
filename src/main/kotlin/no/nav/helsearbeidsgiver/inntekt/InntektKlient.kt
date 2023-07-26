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
        callId: String,
        navConsumerId: String,
        fnr: String,
        fom: YearMonth,
        tom: YearMonth,
        filter: String = "8-28",
        formaal: String = "Sykepenger",
    ): Map<String, Map<YearMonth, Double>> {
        val request = InntektRequest(
            ident = fnr.tilIdent(),
            maanedFom = fom,
            maanedTom = tom,
            ainntektsfilter = filter,
            formaal = formaal,
        )
            .toJson(InntektRequest.serializer())

        val response = httpClient.post("$baseUrl/api/v1/hentinntektliste") {
            bearerAuth(getAccessToken())
            contentType(ContentType.Application.Json)
            header("Nav-Call-Id", callId)
            header("Nav-Consumer-Id", navConsumerId)

            setBody(request)
        }
            .bodyAsText()
            .fromJson(InntektResponse.serializer())

        return response.tilInntektPerOrgnrOgMaaned()
    }
}

private fun String.tilIdent(): Ident =
    Ident(this, "NATURLIG_IDENT")
