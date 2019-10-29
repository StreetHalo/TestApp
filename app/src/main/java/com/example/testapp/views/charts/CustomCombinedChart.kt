package trade.paper.app.views.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import com.example.testapp.R
import com.github.mikephil.charting.charts.CombinedChart
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs


class CustomCombinedChart : CombinedChart, CustomChart {

    var paddingRightAdaptive = 30f // for adaptive padding for charts
    var countSymbolAfterDots = 2
    private var priceToShow = "5000.0"
    private var rawPriceToShow = "-9999.9"
    private var isRed = false
    var symbol = ""
    var position = Position.LEFT
    var maxVisiblePosition: String?  = null

    var isLongPressDetected = false
    var shouldShowRightLine = false


    private val paddingPopup = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            4f,
            this.resources.displayMetrics
    )

    private val linePaint = Paint()
    private val padding by lazy {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    init {
        linePaint.color = resources.getColor(R.color.rd_plane_blue)
        linePaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    }

    fun updateLastPriceToShow(price: String?, isRedColor: Boolean?) {
        rawPriceToShow = price ?: rawPriceToShow

        priceToShow = price ?: priceToShow

        isRed = isRedColor ?: isRed
    }

    private var startPosX: Float = 0f
    private var isNeedToDrawMarkerAtActionMove = false

    fun isLongPressAction() : Boolean {
        return isNeedToDrawMarkerAtActionMove || isLongPressDetected
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
       // Log.d("action", parseEventAction(event))

        event?.let {
            position = if(event.x > viewPortHandler.contentWidth()/2)
                Position.LEFT
            else
                Position.RIGHT

            when(event.action) {
                MotionEvent.ACTION_DOWN ->{
                    startPosX = event.x
                }
                MotionEvent.ACTION_UP -> {
                    isLongPressDetected = false
                    isDragEnabled = true
                    isNeedToDrawMarkerAtActionMove = false
                    startPosX = -11f
                }
            }

            if (isLongPressDetected) {
                val rounded = roundX(highestVisibleX)
                val x = rounded == maxVisiblePosition
                if ((abs(event.x - startPosX) > 10 || it.action == MotionEvent.ACTION_DOWN) && !x) {
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
                if(event.x < viewPortHandler.contentWidth() && event.x > 0) {
                    highlightValue(getHighlightByTouchPoint(event.x, event.y))
                    mSelectionListener.onValueSelected(getEntryByTouchPoint(event.x, event.y), getHighlightByTouchPoint(event.x, event.y))
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun roundX(x: Float): String{
        return BigDecimal(x.toDouble()).setScale(1, RoundingMode.HALF_UP).toPlainString()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        val textPaint = Paint()
        textPaint.color = Color.WHITE
        val spTextSize = 10f
        val textSize = spTextSize * this.resources.displayMetrics.scaledDensity
        textPaint.textSize = textSize

        val rectPaint = Paint()
        rectPaint.style = Paint.Style.FILL
        rectPaint.color = if (isRed) resources.getColor(R.color.red) else resources.getColor(R.color.green)


        canvas?.apply {
            // Calculate by proportion from bottom
            val deltaFromBottom = (viewPortHandler.contentHeight()) * (rawPriceToShow.toFloat() - axisRight.axisMinimum) / (axisRight.axisMaximum - axisRight.axisMinimum)

            // tv last price
            if (axisRight.mAxisMinimum < rawPriceToShow.toFloatOrNull()?:Float.MAX_VALUE) {
                drawRoundRect(RectF(viewPortHandler.contentWidth() + paddingPopup + 2f,
                        viewPortHandler.contentHeight() - deltaFromBottom - textSize / 2,
                        viewPortHandler.contentWidth() + textPaint.measureText(priceToShow) + 2f + 3 * paddingPopup,
                        viewPortHandler.contentHeight() - deltaFromBottom + paddingPopup + textSize / 2),
                        2f, 2f, rectPaint)
                drawText(priceToShow, viewPortHandler.contentWidth() + 2 * paddingPopup + 2f,
                        viewPortHandler.contentHeight() - deltaFromBottom + textSize / 2, textPaint)
            }

            drawLine(0f, viewPortHandler.contentHeight() + padding, viewPortHandler.contentWidth(), viewPortHandler.contentHeight() + padding, linePaint)

            if(shouldShowRightLine) {
                drawLine(viewPortHandler.contentWidth(),0f,viewPortHandler.contentWidth(), viewPortHandler.contentHeight() + padding, linePaint )
            }
        }
    }

    override fun getPaddingRightAdapted(): Float = paddingRightAdaptive

    override fun getWidthChart(): Float = width.toFloat()

    override fun getHeightChart(): Float = height.toFloat()

    fun getFastScrollXPos()  = viewPortHandler.contentWidth()// - padding - 16.dip()
    fun getFastScrollYPos() = viewPortHandler.contentHeight()// - padding


    enum class Position {
        LEFT, RIGHT
    }
}