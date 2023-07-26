package no.nav.helsearbeidsgiver.inntekt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Month
import java.time.YearMonth

class InntektUtilsKtTest : FunSpec({

    context("tilInntektPerOrgnrOgMaaned") {
        test("mapper korrekt") {
            val expected = mapOf(
                UtilsMock.ORGNR_1 to mapOf(
                    YearMonth.of(2020, Month.JANUARY) to (1.0 + 20.0 + 300.0),
                    YearMonth.of(2020, Month.FEBRUARY) to 4000.0,
                    YearMonth.of(2020, Month.MARCH) to 600_000.0,
                ),
                UtilsMock.ORGNR_2 to mapOf(
                    YearMonth.of(2020, Month.FEBRUARY) to (50_000.0 + 7_000_000.0),
                    YearMonth.of(2020, Month.MARCH) to 80_000_000.0,
                    YearMonth.of(2020, Month.APRIL) to 900_000_000.0,
                ),
            )

            val actual = UtilsMock.respons.tilInntektPerOrgnrOgMaaned()

            actual shouldBe expected
        }

        test("fjerner verdier uten orgnr, maaned eller inntekt") {
            val expected = mapOf(
                UtilsMock.ORGNR_1 to mapOf(
                    YearMonth.of(1920, Month.JANUARY) to 1.0,
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
            mockInntektPerMaaned(
                maaned = YearMonth.of(2020, Month.JANUARY),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_1, inntekt = 1.0),
                    mockInntektPerVirksomhet(orgnr = ORGNR_1, inntekt = 20.0),
                ),
            ),
            mockInntektPerMaaned(
                maaned = YearMonth.of(2020, Month.JANUARY),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_1, inntekt = 300.0),
                ),
            ),
            mockInntektPerMaaned(
                maaned = YearMonth.of(2020, Month.FEBRUARY),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_1, inntekt = 4000.0),
                    mockInntektPerVirksomhet(orgnr = ORGNR_2, inntekt = 50_000.0),
                ),
            ),
            mockInntektPerMaaned(
                maaned = YearMonth.of(2020, Month.MARCH),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_1, inntekt = 600_000.0),
                ),
            ),
            mockInntektPerMaaned(
                maaned = YearMonth.of(2020, Month.FEBRUARY),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_2, inntekt = 7_000_000.0),
                ),
            ),
            mockInntektPerMaaned(
                maaned = YearMonth.of(2020, Month.MARCH),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_2, inntekt = 80_000_000.0),
                ),
            ),
            mockInntektPerMaaned(
                maaned = YearMonth.of(2020, Month.APRIL),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_2, inntekt = 900_000_000.0),
                ),
            ),
        ),
    )

    val responsMedUfullstendigeInntekter = InntektResponse(
        arbeidsInntektMaaned = listOf(
            // OK
            mockInntektPerMaaned(
                maaned = YearMonth.of(1920, Month.JANUARY),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_1, inntekt = 1.0),
                ),
            ),
            // Orgnr mangler
            mockInntektPerMaaned(
                maaned = YearMonth.of(1920, Month.FEBRUARY),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = null, inntekt = 20.0),
                ),
            ),
            // Måned mangler
            mockInntektPerMaaned(
                maaned = null,
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_1, inntekt = 300.0),
                ),
            ),
            // Inntekt mangler
            mockInntektPerMaaned(
                maaned = YearMonth.of(1920, Month.APRIL),
                inntekter = listOf(
                    mockInntektPerVirksomhet(orgnr = ORGNR_1, inntekt = null),
                ),
            ),
        ),
    )
}

private fun mockInntektPerMaaned(maaned: YearMonth?, inntekter: List<InntektPerVirksomhet>): InntekterPerMaaned =
    InntekterPerMaaned(
        aarMaaned = maaned,
        arbeidsInntektInformasjon = Inntekter(
            inntektListe = inntekter,
        ),
    )

private fun mockInntektPerVirksomhet(orgnr: String?, inntekt: Double?): InntektPerVirksomhet =
    InntektPerVirksomhet(
        beloep = inntekt,
        virksomhet = Ident(
            identifikator = orgnr,
            aktoerType = null,
        ),
    )
