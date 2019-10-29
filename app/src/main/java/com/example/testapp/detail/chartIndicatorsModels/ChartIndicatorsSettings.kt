package com.hittechsexpertlimited.hitbtc.fragments.detail.chartIndicatorsModels

data class ChartIndicatorsSettings(
        var maEnabled: Boolean = false,
        var emaEnabled: Boolean = false,
        var bbEnabled: Boolean = false,
        var volEnabled: Boolean = true,
        var macdEnabled: Boolean = false,
        var rsiEnabled: Boolean = false,
        val maSettings: MASettings = MASettings(),
        val emaSettings: EMASettings = EMASettings(),
        val bbSettings: BBSettings = BBSettings(),
        val volSettings: VolSettings = VolSettings(),
        val macdSettings: MACDSettings = MACDSettings(),
        val rsiSettings: RSISettings = RSISettings()
)


data class MASettings(
        var length: Int = DefaultIndicatorParams.MA_LENGTH,
        var source: SourceParameter = SourceParameter.Close,
        var offset: Int = DefaultIndicatorParams.MA_OFFSET
)

data class EMASettings(
        var length: Int = DefaultIndicatorParams.EMA_LENGTH,
        var source: SourceParameter = DefaultIndicatorParams.SOURCE,
        var offset: Int = DefaultIndicatorParams.EMA_OFFSET
)

data class BBSettings (
        var length: Int = DefaultIndicatorParams.BB_LENGTH,
        var mult: Int = DefaultIndicatorParams.BB_MULT
)

data class VolSettings(
        var maEnabled: Boolean = DefaultIndicatorParams.VOL_MA,
        var offset: Int = DefaultIndicatorParams.VOL_OFFSET
)

data class MACDSettings(
        var fastLength: Int = DefaultIndicatorParams.MACD_FAST_LENGTH,
        var showLength: Int = DefaultIndicatorParams.MACD_SHOW_LENGTH,
        var source: SourceParameter = DefaultIndicatorParams.SOURCE,
        var signalLength: Int = DefaultIndicatorParams.MACD_SIGNAL_LENGTH
)

data class RSISettings(
        var length: Int = DefaultIndicatorParams.RSI_LENGTH
)



object DefaultIndicatorParams {
    val MA_LENGTH = 9
    val MA_OFFSET = 0

    val EMA_LENGTH = 9
    val EMA_OFFSET = 0

    val BB_LENGTH = 20
    val BB_MULT = 2

    val VOL_MA = false
    val VOL_OFFSET = 0

    val MACD_FAST_LENGTH = 12
    val MACD_SHOW_LENGTH = 26
    val MACD_SIGNAL_LENGTH = 9

    val RSI_LENGTH = 9

    val SOURCE = SourceParameter.Close
}