package no.nav.helsearbeidsgiver.inntekt

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import no.nav.helsearbeidsgiver.utils.test.date.april
import no.nav.helsearbeidsgiver.utils.test.date.juni
import no.nav.helsearbeidsgiver.utils.test.date.mai
import no.nav.helsearbeidsgiver.utils.test.resource.readResource
import java.time.YearMonth

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

        val klient = mockInntektKlient("response.json".readResource(), HttpStatusCode.OK)

        val actualInntekt = klient.hentInntektPerOrgnrOgMaaned("ident", Mock.FOM, Mock.TOM, Mock.CONSUMER_ID, Mock.CALL_ID)

        actualInntekt shouldBe expectedInntekt
    }

    test("BadRequest gir ClientRequestException med status BadRequest") {
        val klient = mockInntektKlient("", HttpStatusCode.BadRequest)

        val e = shouldThrowExactly<ClientRequestException> {
            klient.hentInntektPerOrgnrOgMaaned("ident", Mock.FOM, Mock.TOM, Mock.CONSUMER_ID, Mock.CALL_ID)
        }

        e.response.status shouldBe HttpStatusCode.BadRequest
    }

    test("InternalServerError gir ServerResponseException med status InternalServerError") {
        val klient = mockInntektKlient("", HttpStatusCode.InternalServerError)

        val e = shouldThrowExactly<ServerResponseException> {
            klient.hentInntektPerOrgnrOgMaaned("ident", Mock.FOM, Mock.TOM, Mock.CONSUMER_ID, Mock.CALL_ID)
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
