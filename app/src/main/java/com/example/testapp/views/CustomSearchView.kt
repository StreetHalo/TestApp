package trade.paper.app.views

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.testapp.R
import trade.paper.app.utils.extensions.gone
import trade.paper.app.utils.extensions.visible





open class CustomSearchView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    open var horizontalPadding = 16
    open var etMarginStart = 48
    open var shouldAnimate = true

    private val IMAGE_SEARCH = R.drawable.ic_search_white
    private val IMAGE_BACK = R.drawable.ic_arrow_back_white
    private val IMAGE_CROSS = R.drawable.ic_cross

    private val searchText: EditText
    private val leftImage: ImageView
    private val crossImage: ImageView

    private var showOnlyCross = false

    private var isSearch = false

    var onTextChanged: ((String) -> Unit)? = null

    var onFocusAccept: ((Boolean) -> Unit) = {}

    var backAction: (() -> Unit)? = null

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setBackgroundResource(R.drawable.search_view_background)
        isFocusableInTouchMode = true

        searchText = getEditText().apply {
            attrs?.let {
                val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomSearchView)
                hint = typedArray.getString(R.styleable.CustomSearchView_csv_hintText) ?: ""
                showOnlyCross = typedArray.getBoolean(R.styleable.CustomSearchView_csv_showOnlyCross, false)
                typedArray.recycle()
            }
            setHintTextColor(color(R.color.gray_50))
            onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    this@CustomSearchView.isSelected = true
                    if (shouldAnimate && !showOnlyCross){
                        isSearch = false
                        animateChangeImage(IMAGE_BACK)
                    }
                } else {
                    this@CustomSearchView.isSelected = false
                    if (shouldAnimate && !showOnlyCross) {
                        isSearch = true
                        animateChangeImage(IMAGE_SEARCH)
                    }
                    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(this.getWindowToken(), 0)
                }

                onFocusAccept(!hasFocus)
            }

            setOnClickListener {
                onFocusAccept(!hasFocus())
            }
            background = null
            imeOptions = EditorInfo.IME_ACTION_DONE
            gravity = Gravity.CENTER_VERTICAL
        }

        leftImage = getImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.START or Gravity.CENTER_VERTICAL)
            isSearch = if(shouldAnimate && !showOnlyCross){
                setImageResource(IMAGE_SEARCH)
                true
            } else{
                setImageResource(IMAGE_BACK)
                false

            }

            setOnClickListener {
                if(searchText.text.toString()=="")backAction?.invoke()
                searchText.clearFocus()
                searchText.setText("")
            }
        }

        crossImage = getImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.END or Gravity.CENTER_VERTICAL)
            setImageResource(IMAGE_CROSS)
            setOnClickListener {
                searchText.text.clear()
            }
            gone()
        }

        searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    if (crossImage.visibility == View.VISIBLE) hideCross()
                } else {
                    if (crossImage.visibility == View.GONE) showCross()
                }
                onTextChanged?.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        addView(searchText)
        addView(leftImage)
        addView(crossImage)
    }

    fun getText() = searchText.text.toString()

    fun requestEditTextFocus(){
        Handler(Looper.getMainLooper()).post{
            searchText.requestFocus()
            searchText.setSelection(searchText.text.length)
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    fun setCursorVisible(isVisible: Boolean) {
        searchText.isCursorVisible = isVisible
    }

    fun clear() = searchText.text.clear()

    fun setText(text: String) {
        searchText.setText(text)
    }

    override fun onDetachedFromWindow() {
        crossImage.animate().cancel()
        leftImage.animate().cancel()
        searchText.onFocusChangeListener = null
        super.onDetachedFromWindow()
    }

    private fun getEditText(): EditText {
        return EditText(context).apply {
            val margin = getDip(56)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL).apply {
                marginStart = getDip(etMarginStart)
            }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setHintTextColor(color(R.color.gray_50))
            setTextColor(color(R.color.gray_light))
            setTypeface(ResourcesCompat.getFont(context, R.font.roboto_rg))
            imeOptions = EditorInfo.IME_ACTION_DONE
            setSingleLine(true)
        }
    }

    open fun getImageView(context: Context) = ImageView(context).apply {
        val horizontalPadding = getDip(horizontalPadding)
        val verticalPadding = getDip(8)
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)

        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        setBackgroundResource(outValue.resourceId)
    }

    fun color(color: Int) = ContextCompat.getColor(context, color)

    private fun animateChangeImage(toImage: Int) {
        leftImage.animate().alpha(0f).setDuration(100).withEndAction {
            leftImage.setImageResource(toImage)
            leftImage.animate().alpha(1f).setDuration(100)
        }
    }

    fun changeImageImmediately(toImage: Int){
        leftImage.setImageResource(toImage)
    }

    private fun showCross() {
        crossImage.visible()
        crossImage.animate().alpha(1f).setDuration(200)
    }

    private fun hideCross() {
        crossImage.animate().alpha(0f).setDuration(200).withEndAction {
            crossImage.gone()
        }
    }

    fun hideCrossImmediately(){
        crossImage.gone()
    }

    fun getDip(value: Int) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()
}