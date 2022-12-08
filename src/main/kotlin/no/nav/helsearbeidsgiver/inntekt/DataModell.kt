package no.nav.helsearbeidsgiver.inntekt

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@Serializable
data class HentInntektListeRequest(
    val ident: Ident,
    val ainntektsfilter: String,
    val maanedFom: String? = null,
    val maanedTom: String? = null,
    val formaal: String
)

@Serializable
data class InntektskomponentResponse(
    val arbeidsInntektMaaned: List<ArbeidsinntektMaaned>? = null,
    val ident: Ident? = null
)

@Serializable
data class ArbeidsinntektMaaned(
    @Serializable(with = YearMonthSerializer::class)
    val aarMaaned: YearMonth? = null,
    val avvikListe: List<Avvik>?,
    val arbeidsInntektInformasjon: ArbeidsInntektInformasjon? = null
)

@Serializable
data class Avvik(
    val ident: Ident? = null,
    val opplysningspliktig: Ident? = null,
    val virksomhet: Ident? = null,
    @Serializable(with = YearMonthSerializer::class)
    val avvikPeriode: YearMonth? = null,
    val tekst: String? = null
)

@Serializable
data class Ident(
    val identifikator: String? = null,
    val aktoerType: String? = null
)

@Serializable
data class ArbeidsInntektInformasjon(
    val arbeidsforholdListe: List<ArbeidsforholdFrilanser>? = null,
    val inntektListe: List<Inntekt>? = null,
    val forskuddstrekkListe: List<Forskuddstrekk>? = null,
    val fradragListe: List<Fradrag>? = null
)

@Serializable
data class ArbeidsforholdFrilanser(
    val antallTimerPerUkeSomEnFullStillingTilsvarer: Double? = null,
    val arbeidstidsordning: String? = null,
    val avloenningstype: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val sisteDatoForStillingsprosentendring: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val sisteLoennsendring: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val frilansPeriodeFom: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val frilansPeriodeTom: LocalDate? = null,
    val stillingsprosent: Double? = null,
    val yrke: String? = null,
    val arbeidsforholdID: String? = null,
    val arbeidsforholdIDnav: String? = null,
    val arbeidsforholdstype: String? = null,
    val arbeidsgiver: Ident? = null,
    val arbeidstaker: Ident? = null
)

@Serializable
data class Arbeidsforhold(
    val antallTimerPerUkeSomEnFullStillingTilsvarer: Double? = null,
    val arbeidstidsordning: String? = null,
    val avloenningstype: String? = null,
    @Serializable(with = YearMonthSerializer::class)
    val sisteDatoForStillingsprosentendring: YearMonth? = null,
    @Serializable(with = YearMonthSerializer::class)
    val sisteLoennsendring: YearMonth? = null,
    @Serializable(with = LocalDateSerializer::class)
    val frilansperiodeFom: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val frilansperiodeTom: LocalDate? = null,
    val stillingsprosent: Double? = null,
    val yrke: String? = null,
    val arbeidsforholdID: String? = null,
    val arbeidsforholdIDnav: String? = null,
    val arbeidsforholdType: String? = null,
    val arbeidsgiver: Ident? = null,
    val arbeidstaker: Ident? = null
)

@Serializable
data class Inntekt(
    val inntektType: String? = null,
    val beloep: Double? = null,
    val fordel: String? = null,
    val inntektskilde: String? = null,
    val inntektsperiodetype: String? = null,
    val inntektsstatus: String? = null,
    @Serializable(with = YearMonthSerializer::class)
    val leveringstidspunkt: YearMonth? = null,
    @Serializable(with = YearMonthSerializer::class)
    val utbetaltIMaaned: YearMonth? = null,
    val arbeidsforholdREF: String? = null,
    val opplysningspliktig: Ident? = null,
    val virksomhet: Ident? = null,
    val inntektsmottaker: Ident? = null,
    val inngaarIGrunnlagForTrekk: Boolean? = null,
    val utloeserArbeidsgiveravgift: Boolean? = null,
    val informasjonsstatus: String? = null,
    val beskrivelse: String? = null,
    val skatteOgAvgiftsregel: String? = null,
    val opptjeningsland: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val opptjeningsperiodeFom: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val opptjeningsperiodeTom: LocalDate? = null,
    val skattemessigBosattLand: String? = null,
    val tilleggsinformasjon: Tilleggsinformasjon? = null
)

enum class InntektType {
    LOENNSINNTEKT,
    NAERINGSINNTEKT,
    PENSJON_ELLER_TRYGD,
    YTELSE_FRA_OFFENTLIGE
}

enum class TilleggsinformasjonDetaljerType {
    ALDERSUFOEREETTERLATTEAVTALEFESTETOGKRIGSPENSJON,
    BARNEPENSJONOGUNDERHOLDSBIDRAG,
    BONUSFRAFORSVARET,
    ETTERBETALINGSPERIODE,
    INNTJENINGSFORHOLD,
    REISEKOSTOGLOSJI,
    SVALBARDINNTEKT
}

@Serializable
data class Forskuddstrekk(
    val beloep: Int? = null,
    val beskrivelse: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val leveringstidspunkt: LocalDateTime? = null,
    val opplysningspliktig: Ident? = null,
    val utbetaler: Ident? = null,
    val forskuddstrekkGjelder: Ident? = null
)

@Serializable
data class Fradrag(
    @Serializable(with = BigDecimalSerializer::class)
    val beloep: BigDecimal? = null,
    val beskrivelse: String? = null,
    val fradragsperiode: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val leveringstidspunkt: LocalDateTime? = null,
    val inntektspliktig: Ident? = null,
    val utbetaler: Ident? = null,
    val fradragGjelder: Ident? = null
)

@Serializable
data class Tilleggsinformasjon(
    val kategori: String? = null,
    val tilleggsinformasjonDetaljer: TilleggsinformasjonDetaljer? = null
)

@Serializable
data class TilleggsinformasjonDetaljer(
    val detaljerType: TilleggsinformasjonDetaljerType? = null
)
