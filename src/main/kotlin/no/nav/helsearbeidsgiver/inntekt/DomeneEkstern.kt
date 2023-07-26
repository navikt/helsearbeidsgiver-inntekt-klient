@file:UseSerializers(YearMonthSerializer::class)

package no.nav.helsearbeidsgiver.inntekt

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import no.nav.helsearbeidsgiver.utils.json.serializer.YearMonthSerializer
import java.time.YearMonth

@Serializable
@OptIn(ExperimentalSerializationApi::class)
internal data class InntektRequest(
    val ident: Ident,
    val maanedFom: YearMonth,
    val maanedTom: YearMonth,
) {
    @EncodeDefault
    val ainntektsfilter = "8-28"

    @EncodeDefault
    val formaal = "Sykepenger"
}

@Serializable
internal data class InntektResponse(
    val arbeidsInntektMaaned: List<InntekterPerMaaned>? = null,
)

@Serializable
internal data class InntekterPerMaaned(
    val aarMaaned: YearMonth? = null,
    val arbeidsInntektInformasjon: Inntekter? = null,
)

@Serializable
internal data class Inntekter(
    val inntektListe: List<InntektPerVirksomhet>? = null,
)

@Serializable
internal data class InntektPerVirksomhet(
    val virksomhet: Ident? = null,
    val beloep: Double? = null,
)

@Serializable
internal data class Ident(
    val identifikator: String? = null,
    val aktoerType: String? = null,
)
