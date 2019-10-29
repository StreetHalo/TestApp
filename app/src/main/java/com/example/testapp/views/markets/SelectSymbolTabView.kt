package trade.paper.app.views.markets

import trade.paper.app.models.dto.SymbolDTO
import trade.paper.app.views.BaseView

interface SelectSymbolTabView : BaseView {
    open fun showSymbols(symbols: List<SymbolDTO>, indices: HashMap<String, Int>)
}