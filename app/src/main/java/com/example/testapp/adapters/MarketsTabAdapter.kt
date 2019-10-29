package trade.paper.app.adapters

import android.os.Parcelable
import androidx.fragment.app.FragmentManager


open class MarketsTabAdapter(var manager: FragmentManager) : TradesTabAdapter(manager) {
    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {

    }
}