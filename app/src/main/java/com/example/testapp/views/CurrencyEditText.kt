package trade.paper.app.views

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import com.example.testapp.Formatter
import com.example.testapp.R
import java.lang.Exception
import java.lang.StringBuilder
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.regex.Pattern

class CurrencyEditText : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val editText: PostfixEditText
    private val editTextLayout: FrameLayout
    private val btnMinus: TextView

    private var btnPlus: TextView
    private var isViewEnabled = true

    private var incrSize = BigDecimal.ZERO

    private var numberAfterDot = 0

    private var decimalFormat = DecimalFormat()

    var onFocusChange: ((Boolean) -> Unit)? = null

    init {
        isFocusableInTouchMode = true

        btnMinus = TextView(context).apply {
            val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
            layoutParams = FrameLayout.LayoutParams(width, LayoutParams.MATCH_PARENT, Gravity.START)
            gravity = Gravity.CENTER
            text = "—"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setBackgroundResource(R.drawable.btn_minus_place_order_exchange_background)
            setOnClickListener {
                if (isViewEnabled) increaseValue(-incrSize)
            }
        }

        btnPlus = TextView(context).apply {
            val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
            layoutParams = FrameLayout.LayoutParams(width, LayoutParams.MATCH_PARENT, Gravity.END)
            gravity = Gravity.CENTER
            text = "+"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setBackgroundResource(R.drawable.btn_minus_place_order_exchange_background)
            setOnClickListener {
                if (isViewEnabled) increaseValue(incrSize)
            }
        }

        editText = PostfixEditText(context).apply {
            layoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.CENTER).apply {
                background = null
                gravity = Gravity.CENTER
                imeOptions = EditorInfo.IME_ACTION_DONE
                inputType = InputType.TYPE_CLASS_NUMBER
                setTextColor(ContextCompat.getColor(context, R.color.gray_light))
                setHintTextColor(ContextCompat.getColor(context, R.color.gray_50))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                hint = "0.0"
            }
            onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm!!.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                    onFocusChange?.invoke(hasFocus)
                }
            }
        }

        editTextLayout = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46f, resources.displayMetrics).toInt()
                setMargins(margin, 0, margin, 0)
                setBackgroundResource(R.drawable.et_place_order_exchange_background)
            }
            setOnClickListener {
                if (editText.isEnabled) {
                    editText.requestFocus()
                }
            }
        }

        addView(btnMinus)
        addView(btnPlus)
        addView(editTextLayout)
        editTextLayout.addView(editText)
    }

    private fun increaseValue(value: BigDecimal) {
        try {
            val src = editText.text.toString().toBigDecimal()
            setValue(src + value)
        } catch (e: Exception) {
            if (editText.text.isEmpty()) {
                setValue(value)
            }
        }
    }

    fun setValue(value: BigDecimal) {
        editText.setText(decimalFormat.format(value))
    }

    fun clearEditTextFocus() {
        editText.clearFocus()
    }

    fun setViewEnabled(state: Boolean) {
        editText.isEnabled = state
        isViewEnabled = state
    }

    fun setPostfix(postfix: String) {
        editText.setPostfix(postfix)
    }

    fun setPostfixTextColor(color: Int) {
        editText.setPostfixTextColor(color)
    }

    fun setPostfixTextColor(color: ColorStateList) {
        editText.setPostfixTextColor(color)
    }

    fun setTickNumber(tickSize: BigDecimal) {
        numberAfterDot = Formatter.getNumberAfterDot(tickSize)
        editText.filters = arrayOf(DecimalDigitsInputFilter(numberAfterDot))
        val str = StringBuilder()
        for (i in 0 until numberAfterDot)
            str.append("#")
        decimalFormat.applyPattern("#.${str}")
        incrSize = tickSize
    }

    fun setTextWatcher(textWatcher: TextWatcher) {
        editText.addTextChangedListener(textWatcher)
    }

    fun removeTextWatcher(textWatcher: TextWatcher) {
        editText.removeTextChangedListener(textWatcher)
    }

    fun getText() = editText.text.toString()

    inner class DecimalDigitsInputFilter(digitsAfterZero: Int) : InputFilter {

        internal var mPattern: Pattern

        init {
            mPattern = if (digitsAfterZero == 0) {
                Pattern.compile("[0-9]*")
            } else {
                Pattern.compile("[0-9]+((\\.[0-9]{0,${digitsAfterZero}})?)")
            }
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            if (dstart > dest.indexOf('.')) {
                val matcher = mPattern.matcher("$dest$source")
                return if (!matcher.matches()) {
                    ""
                } else {
                    null
                }
            } else {
                return null
            }
        }
    }
}