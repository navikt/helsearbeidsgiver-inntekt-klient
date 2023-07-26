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
                        it.beloep
                    )
                }
        }
        .filterNotNull()
        .groupBy { (orgnr, _, _) -> orgnr }
        .mapValues { (_, inntektPerOrgnr) ->
            inntektPerOrgnr.groupBy { (_, maaned, _) -> maaned }
                .mapValues { (_, inntektPerMaaned) ->
                    inntektPerMaaned.map { (_, _, inntekt) -> inntekt }
                        .sumMoney()
                }
        }

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
