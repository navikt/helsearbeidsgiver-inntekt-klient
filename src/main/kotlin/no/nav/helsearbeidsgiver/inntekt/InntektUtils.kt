package no.nav.helsearbeidsgiver.inntekt

import no.nav.helsearbeidsgiver.utils.log.logger
import java.time.YearMonth

private val logger = "InntektKlient".logger()

internal fun InntektResponse.tilInntektPerOrgnrOgMaaned(): Map<String, Map<YearMonth, Double>> =
    arbeidsInntektMaaned
        .orEmpty()
        .flatMap { inntekterPerMaaned ->
            inntekterPerMaaned.arbeidsInntektInformasjon
                ?.inntektListe
                .orEmpty()
                .map {
                    Triple(
                        it.virksomhet?.identifikator,
                        inntekterPerMaaned.aarMaaned,
                        it.beloep,
                    )
                }
        }
        .filterNotNull()
        .map(::Inntekt)
        .toMap(Inntekt::orgnr) { inntektPerOrgnr ->
            inntektPerOrgnr.toMap(Inntekt::maaned) { inntektPerMaaned ->
                inntektPerMaaned.map(Inntekt::inntekt).sumMoney()
            }
        }

private data class Inntekt(
    val orgnr: String,
    val maaned: YearMonth,
    val inntekt: Double,
) {
    constructor(inntekt: Triple<String, YearMonth, Double>) : this(inntekt.first, inntekt.second, inntekt.third)
}

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

// TODO Vurder nødvendighet (om eksterne domenemodellfelt må være nullable)
private fun List<Triple<String?, YearMonth?, Double?>>.filterNotNull(): List<Triple<String, YearMonth, Double>> =
    mapNotNull { (orgnr, maaned, inntekt) ->
        if (orgnr == null) {
            logger.warn("Orgnr er null, fjerner element fra inntekter.")
            null
        } else if (maaned == null) {
            logger.warn("Måned er null, fjerner element fra inntekter.")
            null
        } else if (inntekt == null) {
            logger.warn("Inntekt er null, fjerner element fra inntekter.")
            null
        } else {
            Triple(orgnr, maaned, inntekt)
        }
    }
