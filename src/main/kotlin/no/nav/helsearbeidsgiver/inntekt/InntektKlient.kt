package no.nav.helsearbeidsgiver.inntekt

import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.helsearbeidsgiver.tokenprovider.AccessTokenProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InntektKlient(
    private val baseUrl: String,
    private val stsClient: AccessTokenProvider,
    private val httpClient: HttpClient,
) {

    suspend fun hentInntektListe(
        ident: String,
        callId: String,
        navConsumerId: String,
        fraOgMed: LocalDate? = null,
        tilOgMed: LocalDate? = null,
        filter: String = "MedlemskapA-inntekt",
        formaal: String = "Medlemskap"
    ): InntektskomponentResponse {
        val token = stsClient.getToken()
        try {
            return httpClient.post<InntektskomponentResponse>() {
                url("$baseUrl/api/v1/hentinntektliste")
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Id", navConsumerId)
                HentInntektListeRequest(
                    ident = Ident(ident, "NATURLIG_IDENT"),
                    ainntektsfilter = filter,
                    maanedFom = fraOgMed?.tilAarOgMnd(),
                    maanedTom = tilOgMed?.tilAarOgMnd(),
                    formaal = formaal
                )
            }
        } catch (e: Exception) {
            if (e is ClientRequestException) {
                throw InntektKlientException("Fikk status: ${e.response.status} for callId: $callId", e)
            }
            throw InntektKlientException("Klarte ikke hente inntekt for callId: $callId", e)
        }
    }

    private fun LocalDate.tilAarOgMnd() = this.format(DateTimeFormatter.ofPattern("yyyy-MM"))
}

class InntektKlientException(melding: String, exception: java.lang.Exception) : RuntimeException(melding, exception)
