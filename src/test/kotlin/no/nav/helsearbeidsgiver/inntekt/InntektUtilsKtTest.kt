package no.nav.helsearbeidsgiver.inntekt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.utils.test.date.april
import no.nav.helsearbeidsgiver.utils.test.date.februar
import no.nav.helsearbeidsgiver.utils.test.date.januar
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

        test("fjerner verdier uten orgnr, maaned eller inntekt") {
            val expected = mapOf(
                UtilsMock.ORGNR_1 to mapOf(
                    januar(1920) to 1.0,
                ),
            )

            val actual = UtilsMock.responsMedUfullstendigeInntekter.tilInntektPerOrgnrOgMaaned()

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

    val responsMedUfullstendigeInntekter = InntektResponse(
        arbeidsInntektMaaned = listOf(
            // OK
            januar(1920).medInntekter(
                ORGNR_1.medInntekt(1.0),
            ),
            // Orgnr mangler
            februar(1920).medInntekter(
                null.medInntekt(20.0),
            ),
            // Måned mangler
            null.medInntekter(
                ORGNR_1.medInntekt(300.0),
            ),
            // Inntekt mangler
            april(1920).medInntekter(
                ORGNR_1.medInntekt(null),
            ),
        ),
    )
}

private fun YearMonth?.medInntekter(vararg inntekter: InntektPerVirksomhet): InntekterPerMaaned =
    InntekterPerMaaned(
        aarMaaned = this,
        arbeidsInntektInformasjon = Inntekter(
            inntektListe = inntekter.toList(),
        ),
    )

private fun String?.medInntekt(inntekt: Double?): InntektPerVirksomhet =
    InntektPerVirksomhet(
        beloep = inntekt,
        virksomhet = Ident(
            identifikator = this,
            aktoerType = null,
        ),
    )
