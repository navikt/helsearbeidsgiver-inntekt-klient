package no.nav.helsearbeidsgiver.inntekt

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotNull

internal class InntektKlientTest {

    @Test
    fun `Skal returnere response når operasjon var velykket`() {
        val klient = buildClient("response.json".loadFromResources(), HttpStatusCode.Accepted)
        val response:InntektskomponentResponse = runBlocking { klient.hentInntektListe("ident", "call-id", "consumerId") }
        assertNotNull(response)
        assertNotNull(response.arbeidsInntektMaaned)
    }

    @Test
    fun `Skal håndtere forbidden`() {
        assertThrows<InntektKlientException> {
            runBlocking {
                val klient = buildClient("response.json".loadFromResources(), HttpStatusCode.Forbidden)
                klient.hentInntektListe("ident", "call-id", "consumerId")
            }
        }
    }

    @Test
    fun `Skal håndtere BadRequest`() {
        assertThrows<InntektKlientException> {
            runBlocking {
                val klient = buildClient("response.json".loadFromResources(), HttpStatusCode.BadRequest)
                klient.hentInntektListe("ident", "call-id", "consumerId")
            }
        }
    }


    @Test
    fun `Skal håndtere 500`() {
        assertThrows<InntektKlientException> {
            runBlocking {
                val klient = buildClient("response.json".loadFromResources(), HttpStatusCode.InternalServerError)
                klient.hentInntektListe("ident", "call-id", "consumerId")
            }
        }
    }

}
