package no.nav.helsearbeidsgiver.inntekt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.utils.test.date.april
import no.nav.helsearbeidsgiver.utils.test.date.februar
import no.nav.helsearbeidsgiver.utils.test.date.januar
import no.nav.helsearbeidsgiver.utils.test.date.mai
import no.nav.helsearbeidsgiver.utils.test.date.mars
import java.time.YearMonth

class InntektUtilsKtTest : FunSpec({

    context("tilInntektPerOrgnrOgMaaned") {
        test("mapper korrekt") {
            val expected = mapOf(
                UtilsMock.ORGNR_1 to mapOf(
                    januar(2020) to (1.0 + 20.0 + 300.0),
                    februar(2020) to 4000.0,
                    mars(2020) to 600_000.0,
                ),
                UtilsMock.ORGNR_2 to mapOf(
                    februar(2020) to (50_000.0 + 7_000_000.0),
                    mars(2020) to 80_000_000.0,
                    april(2020) to 900_000_000.0,
                ),
            )

            val actual = UtilsMock.respons.tilInntektPerOrgnrOgMaaned()

            actual shouldBe expected
        }

        test("forhindrer avrundingsfeil") {
            val expected = mapOf(
                UtilsMock.ORGNR_1 to mapOf(
                    mai(1867) to 0.3,
                ),
            )

            val actual = UtilsMock.responsMedPotensiellAvrundingsfeil.tilInntektPerOrgnrOgMaaned()

            actual shouldBe expected
        }
    }
})

private object UtilsMock {
    const val ORGNR_1 = "111222333"
    const val ORGNR_2 = "444555666"

    val respons = InntektResponse(
        arbeidsInntektMaaned = listOf(
            januar(2020).medInntekter(
                ORGNR_1.medInntekt(1.0),
                ORGNR_1.medInntekt(20.0),
            ),
            januar(2020).medInntekter(
                ORGNR_1.medInntekt(300.0),
            ),
            februar(2020).medInntekter(
                ORGNR_1.medInntekt(4000.0),
                ORGNR_2.medInntekt(50_000.0),
            ),
            mars(2020).medInntekter(
                ORGNR_1.medInntekt(600_000.0),
            ),
            februar(2020).medInntekter(
                ORGNR_2.medInntekt(7_000_000.0),
            ),
            mars(2020).medInntekter(
                ORGNR_2.medInntekt(80_000_000.0),
            ),
            april(2020).medInntekter(
                ORGNR_2.medInntekt(900_000_000.0),
            ),
        ),
    )

    val responsMedPotensiellAvrundingsfeil = InntektResponse(
        arbeidsInntektMaaned = listOf(
            mai(1867).medInntekter(
                ORGNR_1.medInntekt(0.1),
            ),
            mai(1867).medInntekter(
                ORGNR_1.medInntekt(0.2),
            ),
        ),
    )
}

private fun YearMonth.medInntekter(vararg inntekter: InntektPerVirksomhet): InntekterPerMaaned =
    InntekterPerMaaned(
        aarMaaned = this,
        arbeidsInntektInformasjon = Inntekter(
            inntektListe = inntekter.toList(),
        ),
    )

private fun String.medInntekt(inntekt: Double): InntektPerVirksomhet =
    InntektPerVirksomhet(
        beloep = inntekt,
        virksomhet = Ident(
            identifikator = this,
            aktoerType = null,
        ),
    )
