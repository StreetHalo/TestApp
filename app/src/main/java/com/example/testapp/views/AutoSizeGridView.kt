package trade.paper.app.views

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

class AutoSizeGridView : GridView {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightSpec: Int

        if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE / 2, MeasureSpec.AT_MOST)
        } else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec
        }

        super.onMeasure(widthMeasureSpec, heightSpec)
    }
}

