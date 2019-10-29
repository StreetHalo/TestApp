package trade.paper.app.views.charts

import android.graphics.*
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.util.*


class CustomMarkerView(private val customChart: CustomChart,
                       private val paddingPopup:
                       Float, val textSize: Float) : IMarker {
    var priceV = " "
    var dateV = " "
    var countSymbolAfterDots = 2

    val markerPaint = Paint()
    val linePaint = Paint()
    val textPaint = Paint()
    val rectPaint = Paint()


    init {
        // marker paint at center click

        markerPaint.style = Paint.Style.FILL
        markerPaint.color = Color.argb(70, 255, 255, 255)

        // Dash paint line paint for vertical and horizontal line
        linePaint.isAntiAlias = true
        linePaint.isDither = true
        linePaint.color = Color.argb(70, 255, 255, 255)
        linePaint.style = Paint.Style.STROKE
        linePaint.pathEffect = DashPathEffect(floatArrayOf(5f, 10f), 0f)
        linePaint.strokeWidth = 5f

        // Text Paint at xAxis and yAxis
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize

        // Rect paint under text at aXis and yAxis
        rectPaint.style = Paint.Style.FILL
        rectPaint.color = Color.rgb(73, 73, 73)
    }

    override fun getOffsetForDrawingAtPoint(x: Float, y: Float): MPPointF {
        return MPPointF.getInstance(x - 16.0f, y - 16.0f)
    }

    override fun getOffset(): MPPointF {
        return MPPointF.getInstance(32f, 32f)
    }

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        entry?.let {
          //  priceV = Formatter.amountChart(entry.y.toDouble(), countSymbolAfterDots)
          //  dateV = Formatter.chartDate(entry.data as Date)
        }
    }

    override fun draw(canvas: Canvas?, x: Float, y: Float) {
        canvas?.drawColor(Color.TRANSPARENT)
        val oval1 = RectF(x - 32f, y - 32f, x + 32f, y + 32f)

        val pathHorizontal = Path()
        val pathVertical = Path()
        // Dash effect for line
        pathHorizontal.moveTo(0f, y)
        pathHorizontal.quadTo(10f, y, customChart.getWidthChart() - customChart.getPaddingRightAdapted(), y)

        pathVertical.moveTo(x, 0f)
        pathVertical.quadTo(x, 10f, x, customChart.getHeightChart() - 30f)

        canvas?.apply {
            drawOval(oval1, markerPaint) // Center
            drawPath(pathHorizontal, linePaint) // Dash horizontalLine
            drawPath(pathVertical, linePaint) // Dash verticalLine

            // Price tv
            drawRoundRect(RectF(customChart.getWidthChart() - customChart.getPaddingRightAdapted() + paddingPopup + 2f,
                    y - (textSize / 2) - paddingPopup,
                    customChart.getWidthChart() - customChart.getPaddingRightAdapted() + textPaint.measureText(priceV) + 3 * paddingPopup + 2f,
                    y + (textSize / 2) + paddingPopup),
                    2f, 2f, rectPaint)
            drawText(priceV, customChart.getWidthChart() - customChart.getPaddingRightAdapted() + 2 * paddingPopup + 2f, y + (textSize / 2), textPaint)

            // Date tv
            drawRoundRect(RectF(x - textPaint.measureText(dateV) / 2 - paddingPopup,
                    customChart.getHeightChart() - paddingPopup - textSize,
                    x + textPaint.measureText(dateV) / 2 + paddingPopup,
                    customChart.getHeightChart() + paddingPopup), 2f, 2f, rectPaint)
            drawText(dateV, x - textPaint.measureText(dateV) / 2, customChart.getHeightChart() - (textSize / 2), textPaint)
        }
    }

}
