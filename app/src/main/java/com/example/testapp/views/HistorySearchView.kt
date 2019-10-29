package trade.paper.app.views

import android.content.Context
import android.util.AttributeSet

class HistorySearchView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CustomSearchView(context, attrs, defStyleAttr) {
    override var horizontalPadding: Int
        get() = 12
        set(value) {}

    override var shouldAnimate: Boolean
        get() = false
        set(value) {}

    override var etMarginStart: Int
        get() = 36
        set(value) {}
}