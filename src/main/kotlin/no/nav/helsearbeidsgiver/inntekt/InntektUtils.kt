package no.nav.helsearbeidsgiver.inntekt

import java.time.YearMonth

internal fun InntektResponse.tilInntektPerOrgnrOgMaaned(): Map<String, Map<YearMonth, Double>> =
    arbeidsInntektMaaned
        .orEmpty()
        .flatMap { inntekterPerMaaned ->
            inntekterPerMaaned.arbeidsInntektInformasjon
                ?.inntektListe
                .orEmpty()
                .map {
                    Inntekt(
                        orgnr = it.virksomhet.identifikator,
                        maaned = inntekterPerMaaned.aarMaaned,
                        inntekt = it.beloep,
                    )
                }
        }
        .toMap(Inntekt::orgnr) { inntektPerOrgnr ->
            inntektPerOrgnr.toMap(Inntekt::maaned) { inntektPerMaaned ->
                inntektPerMaaned.map(Inntekt::inntekt).sumMoney()
            }
        }

private data class Inntekt(
    val orgnr: String,
    val maaned: YearMonth,
    val inntekt: Double,
)

private fun <K : Any, V : Any> List<Inntekt>.toMap(
    toKeyGroup: (Inntekt) -> K,
    groupToValue: (List<Inntekt>) -> V,
): Map<K, V> =
    groupBy(toKeyGroup)
        .mapValues { groupToValue(it.value) }

/** Forhindrer avrundingsfeil. */
private fun List<Double>.sumMoney(): Double =
    sumOf(Double::toBigDecimal)
        .toDouble()
