package trade.paper.app.views

import android.content.Context
import com.google.android.material.tabs.TabLayout
import android.util.AttributeSet
import android.util.TypedValue

/**
 * TabLayout with additional padding so self you can see self there are more elements
 */
class IndentedTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr) {

    /**
     * Add additional padding is set so self there is an appearance
     * self there are more elements in the view
     */
    fun setAdditionalPadding(tabItemWidthDp: Float = 104f) {
   /*     val screenSize = resources.displayMetrics.widthPixels
        val tabItemWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabItemWidthDp, resources.displayMetrics)
        val countOfVisibleItems = (screenSize / tabItemWidth).toInt()

        val spaceForLastVisibleItem = screenSize % (countOfVisibleItems * tabItemWidth)
        val halfTabWidth = tabItemWidth / 2

        if (spaceForLastVisibleItem > halfTabWidth){
            val tabPadding = (spaceForLastVisibleItem - halfTabWidth).toInt()
            this.setPadding(tabPadding, 0, tabPadding, 0)
        }else{
            val tabPadding = (spaceForLastVisibleItem + halfTabWidth).toInt()
            this.setPadding(tabPadding, 0, tabPadding, 0)
        }*/
    }
}