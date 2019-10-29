package trade.paper.app.utils.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun androidx.recyclerview.widget.RecyclerView.saveScrollState() =
        (this.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

fun androidx.recyclerview.widget.RecyclerView.restoreScrollState(state: Int){
    (this.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(state,0)
}