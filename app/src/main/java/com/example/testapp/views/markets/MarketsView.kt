package trade.paper.app.views.markets

import trade.paper.app.models.dto.SymbolDTO
import trade.paper.app.views.BaseView

interface MarketsView : BaseView {

    fun symbolsReady(map: HashMap<String, ArrayList<SymbolDTO>>, tabs: HashSet<String>)
}