package trade.paper.app.views

import android.content.Context
import android.util.AttributeSet

class FocusedSearchView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): CustomSearchView(context, attrs, defStyleAttr) {
    override var shouldAnimate: Boolean
        get() = false
        set(value) {}
}