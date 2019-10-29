package trade.paper.app.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView

class InteractiveScrollView : ScrollView {
    // Getters & Setters

    var onBottomReachedListener: OnBottomReachedListener? = null
    var onTopReachedListener: OnTopReachedListener? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val view = getChildAt(childCount - 1) as View
        val diff = view.bottom - (height + scrollY)

        if (diff == 0 && onBottomReachedListener != null) {
            onBottomReachedListener!!.onBottomReached()
        } else if (scrollY == 0 && onTopReachedListener != null) {
            onTopReachedListener!!.onTopReached()
        }

        super.onScrollChanged(l, t, oldl, oldt)
    }



    /**
     * Event listener.
     */
    interface OnBottomReachedListener {
        fun onBottomReached()
    }

    interface OnTopReachedListener {
        fun onTopReached()
    }
}