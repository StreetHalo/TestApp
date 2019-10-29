package trade.paper.app.views.charts

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.mikephil.charting.charts.LineChart
import kotlin.math.abs

class CustomLineChart : LineChart, CustomChart {
    var paddingRightAdaptive = 30f // for adaptive padding for charts
    var countSymbolAfterDots = 2 // for last price implementation
    var isLongPressDetected = false

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun getPaddingRightAdapted(): Float = paddingRightAdaptive

    override fun getWidthChart(): Float = width.toFloat()

    override fun getHeightChart(): Float = height.toFloat()


    private var startPosX: Float = 0f
    private var isNeedToDrawMarkerAtActionMove = false

    override fun onTouchEvent(event: MotionEvent?): Boolean { // Remove эту Copypaste позже PLS @CustomCombinedChart
        parent.requestDisallowInterceptTouchEvent(true)

        event?.let {
            when(event.action) {
                MotionEvent.ACTION_DOWN -> startPosX = event.x
                MotionEvent.ACTION_UP -> {
                    isLongPressDetected = false
                    isDragEnabled = true
                    isNeedToDrawMarkerAtActionMove = false
                    startPosX = 0f
                }
            }

            if (isLongPressDetected) {
                if (abs(event.x - startPosX) > 10) {
                    isLongPressDetected = false
                    isDragEnabled = true
                    isNeedToDrawMarkerAtActionMove = false
                } else {
                    isLongPressDetected = false
                    isDragEnabled = false
                    isNeedToDrawMarkerAtActionMove = true
                }
            }

            if (isNeedToDrawMarkerAtActionMove) {
                highlightValue(getHighlightByTouchPoint(event.x, event.y))
                mSelectionListener.onValueSelected(getEntryByTouchPoint(event.x, event.y), getHighlightByTouchPoint(event.x, event.y))
            }
        }
        return super.onTouchEvent(event)
    }
}