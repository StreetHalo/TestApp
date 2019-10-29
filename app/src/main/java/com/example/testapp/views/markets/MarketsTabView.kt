package trade.paper.app.views.markets

import androidx.fragment.app.FragmentActivity
import trade.paper.app.adapters.MarketsRecyclerAdapter
import trade.paper.app.models.dto.SymbolDTO
import trade.paper.app.views.BaseView

interface MarketsTabView : BaseView {

    fun showSymbols(symbols: List<SymbolDTO>, indices: HashMap<String, Int>)
    fun provideActivity(): FragmentActivity
    fun setAdapter(adapter: MarketsRecyclerAdapter)
}