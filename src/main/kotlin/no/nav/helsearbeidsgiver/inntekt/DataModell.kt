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
    val maanedFom: String?,
    val maanedTom: String?,
    val formaal: String
)

@Serializable
data class InntektskomponentResponse(
    val arbeidsInntektMaaned: List<ArbeidsinntektMaaned>? = null,
    val ident: Ident?
)

@Serializable
data class ArbeidsinntektMaaned(
    @Serializable(with = YearMonthSerializer::class)
    val aarMaaned: YearMonth?,
    val avvikListe: List<Avvik>?,
    val arbeidsInntektInformasjon: ArbeidsInntektInformasjon?
)

@Serializable
data class Avvik(
    val ident: Ident?,
    val opplysningspliktig: Ident?,
    val virksomhet: Ident?,
    @Serializable(with = YearMonthSerializer::class)
    val avvikPeriode: YearMonth?,
    val tekst: String?
)

@Serializable
data class Ident(
    val identifikator: String?,
    val aktoerType: String?
)

@Serializable
data class ArbeidsInntektInformasjon(
    val arbeidsforholdListe: List<ArbeidsforholdFrilanser>?,
    val inntektListe: List<Inntekt>?,
    val forskuddstrekkListe: List<Forskuddstrekk>?,
    val fradragListe: List<Fradrag>?
)

@Serializable
data class ArbeidsforholdFrilanser(
    val antallTimerPerUkeSomEnFullStillingTilsvarer: Double?,
    val arbeidstidsordning: String?,
    val avloenningstype: String?,
    @Serializable(with = LocalDateSerializer::class)
    val sisteDatoForStillingsprosentendring: LocalDate?,
    @Serializable(with = LocalDateSerializer::class)
    val sisteLoennsendring: LocalDate?,
    @Serializable(with = LocalDateSerializer::class)
    val frilansPeriodeFom: LocalDate?,
    @Serializable(with = LocalDateSerializer::class)
    val frilansPeriodeTom: LocalDate?,
    val stillingsprosent: Double?,
    val yrke: String?,
    val arbeidsforholdID: String?,
    val arbeidsforholdIDnav: String?,
    val arbeidsforholdstype: String?,
    val arbeidsgiver: Ident?,
    val arbeidstaker: Ident?
)

@Serializable
data class Arbeidsforhold(
    val antallTimerPerUkeSomEnFullStillingTilsvarer: Double?,
    val arbeidstidsordning: String?,
    val avloenningstype: String?,
    @Serializable(with = YearMonthSerializer::class)
    val sisteDatoForStillingsprosentendring: YearMonth?,
    @Serializable(with = YearMonthSerializer::class)
    val sisteLoennsendring: YearMonth?,
    @Serializable(with = LocalDateSerializer::class)
    val frilansperiodeFom: LocalDate?,
    @Serializable(with = LocalDateSerializer::class)
    val frilansperiodeTom: LocalDate?,
    val stillingsprosent: Double?,
    val yrke: String?,
    val arbeidsforholdID: String?,
    val arbeidsforholdIDnav: String?,
    val arbeidsforholdType: String?,
    val arbeidsgiver: Ident?,
    val arbeidstaker: Ident?
)

@Serializable
data class Inntekt(
    val inntektType: String?,
    val beloep: Double?,
    val fordel: String?,
    val inntektskilde: String?,
    val inntektsperiodetype: String?,
    val inntektsstatus: String?,
    @Serializable(with = YearMonthSerializer::class)
    val leveringstidspunkt: YearMonth?,
    @Serializable(with = YearMonthSerializer::class)
    val utbetaltIMaaned: YearMonth?,
    val arbeidsforholdREF: String?,
    val opplysningspliktig: Ident?,
    val virksomhet: Ident?,
    val inntektsmottaker: Ident?,
    val inngaarIGrunnlagForTrekk: Boolean?,
    val utloeserArbeidsgiveravgift: Boolean?,
    val informasjonsstatus: String?,
    val beskrivelse: String?,
    val skatteOgAvgiftsregel: String?,
    val opptjeningsland: String?,
    @Serializable(with = LocalDateSerializer::class)
    val opptjeningsperiodeFom: LocalDate?,
    @Serializable(with = LocalDateSerializer::class)
    val opptjeningsperiodeTom: LocalDate?,
    val skattemessigBosattLand: String?,
    val tilleggsinformasjon: Tilleggsinformasjon?
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
    val beloep: Int?,
    val beskrivelse: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val leveringstidspunkt: LocalDateTime?,
    val opplysningspliktig: Ident?,
    val utbetaler: Ident?,
    val forskuddstrekkGjelder: Ident?
)

@Serializable
data class Fradrag(
    @Serializable(with = BigDecimalSerializer::class)
    val beloep: BigDecimal?,
    val beskrivelse: String?,
    val fradragsperiode: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val leveringstidspunkt: LocalDateTime?,
    val inntektspliktig: Ident?,
    val utbetaler: Ident?,
    val fradragGjelder: Ident?
)

@Serializable
data class Tilleggsinformasjon(
    val kategori: String?,
    val tilleggsinformasjonDetaljer: TilleggsinformasjonDetaljer?
)

@Serializable
data class TilleggsinformasjonDetaljer(
    val detaljerType: TilleggsinformasjonDetaljerType?
)
