package no.nav.helsearbeidsgiver.inntekt

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.tokenprovider.AccessTokenProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InntektKlient(
    private val baseUrl: String,
    private val stsClient: AccessTokenProvider,
    private val httpClient: HttpClient
) {

    suspend fun hentInntektListe(
        ident: String,
        callId: String,
        navConsumerId: String,
        fraOgMed: LocalDate,
        tilOgMed: LocalDate,
        filter: String = "MedlemskapA-inntekt",
        formaal: String = "Medlemskap"
    ): InntektskomponentResponse {
        val token = stsClient.getToken()
        try {
            val httpResponse: HttpResponse = httpClient.post("$baseUrl/api/v1/hentinntektliste") {
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Id", navConsumerId)
                contentType(ContentType.Application.Json)
                setBody(
                    HentInntektListeRequest(
                        ident = Ident(ident, "NATURLIG_IDENT"),
                        ainntektsfilter = filter,
                        maanedFom = fraOgMed.tilAarOgMnd(),
                        maanedTom = tilOgMed.tilAarOgMnd(),
                        formaal = formaal
                    )
                )
            }
            if (listOf(HttpStatusCode.Accepted).contains(httpResponse.status)) {
                return httpResponse.body()
            }
            throw InntektKlientException("Fikk status: ${httpResponse.status} for callId: $callId", IkkeGodkjentStatus(httpResponse.status.value))
        } catch (ex: Exception) {
            throw InntektKlientException("Fikk feil for callId: $callId", ex)
        }
    }

    private fun LocalDate.tilAarOgMnd() = this.format(DateTimeFormatter.ofPattern("yyyy-MM"))
}

class IkkeGodkjentStatus(status: Int) : RuntimeException("Fikk status $status")

class InntektKlientException(melding: String, throwable: Throwable) : RuntimeException(melding, throwable)
