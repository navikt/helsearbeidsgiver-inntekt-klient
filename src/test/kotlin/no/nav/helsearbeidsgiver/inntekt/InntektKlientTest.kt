package no.nav.helsearbeidsgiver.inntekt

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import java.time.Month
import java.time.YearMonth

class InntektKlientTest : FunSpec({

    test("Returnerer inntekter dersom respons er OK") {
        val expectedInntekt = mapOf(
            "123456785" to mapOf(
                YearMonth.of(2020, Month.APRIL) to (1.0 + 123.0 + 456.0),
                YearMonth.of(2020, Month.MAY) to 789.0,
            ),
            "111111111" to mapOf(
                YearMonth.of(2020, Month.MAY) to (700.0 + 800.0),
                YearMonth.of(2020, Month.JUNE) to 1000.0,
            ),
        )

        val klient = mockInntektKlient("response.json".readResource(), HttpStatusCode.OK)

        val actualInntekt = klient.hentInntektPerOrgnrOgMaaned(Mock.CALL_ID, Mock.CONSUMER_ID, "ident", Mock.FOM, Mock.TOM)

        actualInntekt shouldBe expectedInntekt
    }

    test("BadRequest gir ClientRequestException med status BadRequest") {
        val klient = mockInntektKlient("", HttpStatusCode.BadRequest)

        val e = shouldThrowExactly<ClientRequestException> {
            klient.hentInntektPerOrgnrOgMaaned(Mock.CALL_ID, Mock.CONSUMER_ID, "ident", Mock.FOM, Mock.TOM)
        }

        e.response.status shouldBe HttpStatusCode.BadRequest
    }

    test("InternalServerError gir ServerResponseException med status InternalServerError") {
        val klient = mockInntektKlient("", HttpStatusCode.InternalServerError)

        val e = shouldThrowExactly<ServerResponseException> {
            klient.hentInntektPerOrgnrOgMaaned(Mock.CALL_ID, Mock.CONSUMER_ID, "ident", Mock.FOM, Mock.TOM)
        }

        e.response.status shouldBe HttpStatusCode.InternalServerError
    }
})

private object Mock {
    const val CALL_ID = "mockCallId"
    const val CONSUMER_ID = "mockConsumerId"
    val TOM: YearMonth = YearMonth.now()
    val FOM: YearMonth = TOM.minusMonths(3)
}

private fun String.readResource(): String =
    ClassLoader.getSystemResource(this)?.readText()!!
