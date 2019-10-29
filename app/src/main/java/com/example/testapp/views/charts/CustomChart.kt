package trade.paper.app.views.charts

const val SP_TEXT_POPUP = 10f

interface CustomChart {
    fun getPaddingRightAdapted() : Float
    fun getWidthChart(): Float
    fun getHeightChart(): Float
}