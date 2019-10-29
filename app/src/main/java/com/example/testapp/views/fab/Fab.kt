package trade.paper.app.views.fab

import android.view.View
import androidx.core.view.isVisible
import com.example.testapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import trade.paper.app.utils.extensions.animate
import trade.paper.app.utils.extensions.gone
import trade.paper.app.utils.extensions.visible

interface IFab {
    fun shake()
    fun show()
    fun hide()
    var fabShowed: Boolean
}

class SubscriptionFab(val fab: FloatingActionButton) : IFab{

    override var fabShowed: Boolean = false


    override fun shake() {
        fab.animate(R.anim.shake_animation)
    }

    override fun show() {
        fab.visible()
    }

    override fun hide() {
        fab.gone()
    }

}