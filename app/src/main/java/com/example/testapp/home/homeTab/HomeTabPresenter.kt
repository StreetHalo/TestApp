package trade.paper.app.fragments.home.homeTab

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.dto.SymbolDTO
import trade.paper.app.models.hawk.Settings

class HomeTabPresenter : HomeTabContact.Presenter {

    private val handler: Handler

    private var view: HomeTabContact.View? = null

    private var type = HomeTabFragment.TAB_FAVORITE

    private var data: ArrayList<SymbolDTO>? = null

    private var filter = ""

    init {
        val thread = HandlerThread("background")
        thread.start()
        handler = Handler(thread.looper)
    }

    override fun attachView(view: HomeTabContact.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun getData(type: Int) {
        this.type = type
        data = arrayListOf()
        val list = when (type) {
         /*   HomeTabFragment.TAB_TOP_VAL_24H -> {
                data?.clear()
                data?.addAll(RXCache.getAllSymbolsDto().sortedByDescending { it.volume })
                data
            }*/
            HomeTabFragment.TAB_GAINERS -> {
                data?.clear()
                data?.addAll(RXCache.getAllSymbolsDto().sortedByDescending { it.change24 })
                data
            }
            HomeTabFragment.TAB_LOSERS -> {
                data?.clear()
                data?.addAll(RXCache.getAllSymbolsDto().sortedBy { it.change24 })
                data
            }
            else -> null
        }

        list?.let {
            if (filter.isBlank()) {
                view?.setData(list)
            } else {
            //    val filtered = list.filter { Utils.searchSymbol(it.symbol, filter) }
            //    view?.setData(filtered)
            }
        }
    }

    override fun onFavoriteClick(index: Int) {

    }

    override fun goToMarkets() {
     //   AnalyticsManager.sendEvent(AnalyticsManager.Event.HomeMarketsClicked)
        view?.goToMarkets(null)
    }

    override fun onItemClick(currency: String) {
        view?.goToMarkets(currency)
    }

    override fun setFilter(filter: String) {
        if (this.filter != filter) {
            this.filter = filter
            val ft = filter.toUpperCase().replace("/", "")
            val data = data ?: RXCache.getFavorites()
           // view?.setData(data.filter { Utils.searchSymbol(it.symbol, ft) })
        }
    }

    override fun favoritesChange(favorites: List<SymbolDTO>) {
        data?.let { data ->
            handler.post {
                favorites.forEach { favorite ->
                    val index = data.indexOf(data.find { it.symbol == favorite.symbol })
                    Handler(Looper.getMainLooper()).post {
                        view?.notifyItemChanged(index)
                    }
                }
            }
        }
    }
}