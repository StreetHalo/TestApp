package trade.paper.app.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.widget.EditText
import com.example.testapp.R


class PostfixEditText : EditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val COLOR_DEFAULT = R.color.gray_50

    private var mPrefixTextColor: ColorStateList = ColorStateList.valueOf(ContextCompat.getColor(context, COLOR_DEFAULT))


    fun setPostfix(postfix: String) {
        setCompoundDrawables(null, null, TextDrawable(postfix), null)
        compoundDrawablePadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
    }

    fun setPostfixTextColor(color: Int) {
        mPrefixTextColor = ColorStateList.valueOf(color)
    }

    fun setPostfixTextColor(color: ColorStateList) {
        mPrefixTextColor = color
    }

    private inner class TextDrawable(text: String) : Drawable() {
        private var mText = ""

        init {
            mText = text
            setBounds(0, 0, paint.measureText(mText).toInt() + 2, textSize.toInt())
        }

        override fun draw(canvas: Canvas) {
            val paint = getPaint()
            paint.color = mPrefixTextColor.getColorForState(drawableState, 0)
            val lineBaseline = getLineBounds(0, null)
            canvas.drawText(mText, 0f, (canvas.getClipBounds().top + lineBaseline).toFloat(), paint)
        }

        override fun setAlpha(alpha: Int) {/* Not supported */
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {/* Not supported */
        }

        override fun getOpacity(): Int {
            return PixelFormat.OPAQUE
        }
    }
}