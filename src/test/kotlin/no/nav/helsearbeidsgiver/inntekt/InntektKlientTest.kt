@file:Suppress("NonAsciiCharacters")

package no.nav.helsearbeidsgiver.inntekt

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.assertNotNull

internal class InntektKlientTest {

    val TIL = LocalDate.now()
    val FRA = TIL.minusDays(90)
    val FILTER = ""
    val FORMAAL = ""

    @Test
    fun `Skal returnere response når operasjon var velykket`() {
        val klient = BuildClient("response.json".loadFromResources(), HttpStatusCode.Accepted)
        val response: InntektskomponentResponse = runBlocking { klient.hentInntektListe("ident", "call-id", "consumerId", FRA, TIL, FILTER, FORMAAL) }
        assertNotNull(response)
        assertNotNull(response.arbeidsInntektMaaned)
    }

    @Test
    fun `Skal håndtere forbidden`() {
        assertThrows<InntektKlientException> {
            runBlocking {
                val klient = BuildClient("response.json".loadFromResources(), HttpStatusCode.Forbidden)
                klient.hentInntektListe("ident", "call-id", "consumerId", FRA, TIL, FILTER, FORMAAL)
            }
        }
    }

    @Test
    fun `Skal håndtere BadRequest`() {
        assertThrows<InntektKlientException> {
            runBlocking {
                val klient = BuildClient("response.json".loadFromResources(), HttpStatusCode.BadRequest)
                klient.hentInntektListe("ident", "call-id", "consumerId", FRA, TIL, FILTER, FORMAAL)
            }
        }
    }

    @Test
    fun `Skal håndtere 500`() {
        assertThrows<InntektKlientException> {
            runBlocking {
                val klient = BuildClient("response.json".loadFromResources(), HttpStatusCode.InternalServerError)
                klient.hentInntektListe("ident", "call-id", "consumerId", FRA, TIL, FILTER, FORMAAL)
            }
        }
    }
}
