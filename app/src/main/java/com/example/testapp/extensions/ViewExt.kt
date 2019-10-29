package trade.paper.app.utils.extensions

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.testapp.R


fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View.getScreenWidthSizeInDp(): Float {
    val display = (context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)

    val density = resources.displayMetrics.density
    return outMetrics.widthPixels / density
}

fun ImageView.changeDrawableBackground(context: Context, color: Int){
    DrawableCompat.setTint(
            DrawableCompat.wrap(this.getDrawable()),
            ContextCompat.getColor(context, color)
    )
}

fun showActionDisabled(context: Context?, layout: LinearLayout?, hintAllowed: Boolean){

    layout?.let{
        it.alpha = 0.35f
        (it.getChildAt(0) as ImageView).changeDrawableBackground(context!!, R.color.gray_50)
        (it.getChildAt(1) as TextView).setTextColor(ContextCompat.getColor(context!!, R.color.gray_50))
        it.isClickable = hintAllowed
    }
}

fun showActionEnabled(context: Context?, layout: LinearLayout?){
    layout?.let{
        it.alpha = 1f
        (it.getChildAt(0) as ImageView).changeDrawableBackground(context!!, R.color.action_enabled)
        (it.getChildAt(1) as TextView).setTextColor(ContextCompat.getColor(context!!, R.color.rd_text))
        it.isClickable = true
    }
}

fun showActionDisabled(context: Context?, view: View?, hintAllowed: Boolean){
    view?.alpha = 0.3f
    view?.isClickable = hintAllowed

}
fun View.animate(id: Int) {
    this.startAnimation(AnimationUtils.loadAnimation(context, id))
}