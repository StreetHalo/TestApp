package trade.paper.app.fragments.home.homeTab


import android.content.Intent
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.BaseFragment
import com.example.testapp.R
import com.example.testapp.home.HomeActivity
import com.example.testapp.ui.dashboard.MarketsActivity
import trade.paper.app.adapters.HomeFavoriteRecyclerAdapter
import trade.paper.app.adapters.MarketsRecyclerAdapter
import trade.paper.app.models.TAG
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.dto.SymbolDTO
import kotlinx.android.synthetic.main.fragment_home_tab.*

class HomeTabFragment : BaseFragment(), HomeTabContact.View {

    companion object {
      //  const val TAB_TOP_VAL_24H = 0
      const val TAB_FAVORITE = 0
        const val TAB_GAINERS = 1
        const val TAB_LOSERS = 2
    }

    private val presenter: HomeTabContact.Presenter = HomeTabPresenter()
    private lateinit var rvAdapter: MarketsRecyclerAdapter

    private var type = TAB_FAVORITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            type = it.getInt("type", TAB_FAVORITE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_tab, container, false)
    }

    fun reload() {
        inUI {
            rvAdapter.tickerDisposable?.dispose()
            if (type != TAB_FAVORITE) presenter.getData(type)
            rvAdapter.provideContext(context)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAdapter = if (type == TAB_FAVORITE) HomeFavoriteRecyclerAdapter(onItemClickListener, this::reload) else MarketsRecyclerAdapter(onItemClickListener, this::reload)
        rvAdapter.sortBy = when (type) {
            TAB_GAINERS -> "changeDecr"
            TAB_LOSERS -> "changeIncr"
            else -> ""
        }

        presenter.attachView(this)

        initViews()
        initRecycler()


        RXCache.favorites.observe(this, Observer {
            it?.let {
                inUI {
                    if (type == TAB_FAVORITE) {
                        rv_home_tab.visibility = View.VISIBLE
                        pb_home_tab.visibility = View.GONE
                        rvAdapter.tickerDisposable?.dispose()
                        rvAdapter.data.clear()
                        rvAdapter.data.addAll(RXCache.getFavorites())
                        rvAdapter.notifyDataSetChanged()
                        rvAdapter.subscribeToTicker()
                    } else {
//                    presenter.favoritesChange(it)
                    }
                }
            }
        })

        RXCache.favoriteChange.observe(this, Observer {
            it?.let { symbol ->
                val data = rvAdapter.data

                for (i in 0 until data.size){
                    if (data.getOrNull(i)?.symbol == symbol){
                        inUI {
                            rvAdapter.notifyItemChanged(i)
                        }
                    }
                    break
                }
            }
        })

        if (type != TAB_FAVORITE) presenter.getData(type)
        rvAdapter.provideContext(context)
    }

    private fun initRecycler() {
        rv_home_tab.apply {
            layoutManager = llm()
            setHasFixedSize(true)
            adapter = rvAdapter
            if (itemAnimator is androidx.recyclerview.widget.SimpleItemAnimator)
                (itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false

            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                    rvAdapter.isScrolling = newState != androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
                }
            })
        }

        rvAdapter.onFavoriteBtnClick = {
            presenter.onFavoriteClick(it)
        }

        if (rvAdapter is HomeFavoriteRecyclerAdapter) {
            (rvAdapter as HomeFavoriteRecyclerAdapter).goToMarkets = {
                presenter.goToMarkets()
            }
        }
    }

    private val onItemClickListener = View.OnClickListener {
        presenter.onItemClick(it.tag.toString())
    }

    private fun initViews() {
        rv_home_tab.visibility = View.GONE
        pb_home_tab.visibility = View.VISIBLE
    }

    override fun setData(data: List<SymbolDTO>) {
        rv_home_tab?.visibility = View.VISIBLE
        pb_home_tab?.visibility = View.GONE

        rvAdapter.data.clear()
        rvAdapter.data.addAll(data)
        rvAdapter.notifyDataSetChanged()
        rvAdapter.subscribeToTicker()
    }

    override fun updateItem(index: Int) {
        rvAdapter.notifyItemChanged(index)
    }

    override fun goToMarkets(currency: String?) {

  (activity as HomeActivity).openDetails()

        /*(activity as BaseActivity).apply {
            val fragment: BaseFragment = if (currency == null)
                MarketsFragment()
            else
                SymbolDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("symbol", currency)
                        putString(TAG.PARENT.tag, HomeTabFragment::class.java.simpleName)
                    }
                }
//            clearFlow(BaseActivity.FLOW_MARKETS)
//            openFragment(fragment, BaseActivity.FLOW_MARKETS)
            changeFlow(BaseActivity.FLOW_MARKETS, fragment)
        }*/
    }

    override fun notifyItemChanged(index: Int) {
        rvAdapter.notifyItemChanged(index)
    }

    fun setFilter(filter: String) {
        presenter.setFilter(filter)
    }

    override fun onPause() {
        rvAdapter.tickerDisposable?.dispose()
        rvAdapter.tickerDisposable = null
        super.onPause()
    }

    override fun onStart() {
        reload()
        super.onStart()
    }
}