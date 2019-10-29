package trade.paper.app.fragments.home.homeTab

import trade.paper.app.models.dto.SymbolDTO

interface HomeTabContact {
    interface View {
        fun setData(data: List<SymbolDTO>)
        fun updateItem(index: Int)
        fun goToMarkets(currency: String?)
        fun notifyItemChanged(index: Int)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun getData(type: Int)
        fun onFavoriteClick(index: Int)
        fun goToMarkets()
        fun onItemClick(currency: String)
        fun setFilter(filter: String)
        fun favoritesChange(favorites: List<SymbolDTO>)
    }
}