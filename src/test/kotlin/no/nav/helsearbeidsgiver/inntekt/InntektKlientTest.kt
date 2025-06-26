package no.nav.helsearbeidsgiver.inntekt

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import no.nav.helsearbeidsgiver.utils.test.date.april
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mai
import no.nav.helsearbeidsgiver.utils.test.resource.readResource
import no.nav.helsearbeidsgiver.utils.test.wrapper.genererGyldig
import no.nav.helsearbeidsgiver.utils.wrapper.Fnr
import java.time.YearMonth

private val okResponseJson = "response.json".readResource()

class InntektKlientTest : FunSpec({

    test("Returnerer inntekter dersom respons er OK") {
        val expectedInntekt = mapOf(
            "123456785" to mapOf(
                april(2020) to (1.0 + 123.0 + 456.0),
                mai(2020) to 789.0,
            ),
            "111111111" to mapOf(
                mai(2020) to (700.0 + 800.0),
                juni(2020) to 1000.0,
            ),
        )

        val klient = mockInntektKlient(HttpStatusCode.OK to okResponseJson)

        val actualInntekt = klient.hentInntektPerOrgnrOgMaaned(Fnr.genererGyldig().verdi, Mock.fom, Mock.tom, Mock.CONSUMER_ID, Mock.CALL_ID)

        actualInntekt shouldBe expectedInntekt
    }

    test("feiler ved 4xx-feil") {
        val klient = mockInntektKlient(HttpStatusCode.BadRequest to "")

        val e = shouldThrowExactly<ClientRequestException> {
            klient.hentInntektPerOrgnrOgMaaned(Fnr.genererGyldig().verdi, Mock.fom, Mock.tom, Mock.CONSUMER_ID, Mock.CALL_ID)
        }

        e.response.status shouldBe HttpStatusCode.BadRequest
    }

    test("lykkes ved færre 5xx-feil enn max retries (3)") {
        val klient = mockInntektKlient(
            HttpStatusCode.InternalServerError to "",
            HttpStatusCode.InternalServerError to "",
            HttpStatusCode.InternalServerError to "",
            HttpStatusCode.OK to okResponseJson,
        )

        runTest {
            shouldNotThrowAny {
                klient.hentInntektPerOrgnrOgMaaned(Fnr.genererGyldig().verdi, Mock.fom, Mock.tom, Mock.CONSUMER_ID, Mock.CALL_ID)
            }
        }
    }

    test("feiler ved flere 5xx-feil enn max retries (3)") {
        val klient = mockInntektKlient(
            HttpStatusCode.InternalServerError to "",
            HttpStatusCode.InternalServerError to "",
            HttpStatusCode.InternalServerError to "",
            HttpStatusCode.InternalServerError to "",
        )

        runTest {
            val e = shouldThrowExactly<ServerResponseException> {
                klient.hentInntektPerOrgnrOgMaaned(Fnr.genererGyldig().verdi, Mock.fom, Mock.tom, Mock.CONSUMER_ID, Mock.CALL_ID)
            }

            e.response.status shouldBe HttpStatusCode.InternalServerError
        }
    }

    test("kall feiler og prøver på nytt ved timeout") {
        val klient = mockInntektKlient(
            HttpStatusCode.OK to "timeout",
            HttpStatusCode.OK to "timeout",
            HttpStatusCode.OK to "timeout",
            HttpStatusCode.OK to okResponseJson,
        )

        runTest {
            shouldNotThrowAny {
                klient.hentInntektPerOrgnrOgMaaned(Fnr.genererGyldig().verdi, Mock.fom, Mock.tom, Mock.CONSUMER_ID, Mock.CALL_ID)
            }
        }
    }
})

private object Mock {
    const val CALL_ID = "mockCallId"
    const val CONSUMER_ID = "mockConsumerId"
    val tom: YearMonth = YearMonth.now()
    val fom: YearMonth = tom.minusMonths(3)
}
