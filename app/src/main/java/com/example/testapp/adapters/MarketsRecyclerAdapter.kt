package trade.paper.app.adapters

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.testapp.R
import trade.paper.app.listeners.DataCacheListener
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.dto.SymbolDTO
import trade.paper.app.models.hawk.Settings
import trade.paper.app.models.rpc.params.TickerParams
import io.reactivex.BackpressureOverflowStrategy
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_markets.view.*
import trade.paper.app.utils.extensions.invisible
import java.util.*
import kotlin.math.abs

open class MarketsRecyclerAdapter(var onClickListener: View.OnClickListener?, var reload: () -> Unit?) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>(), DataCacheListener {

    private val MIN_PRICE_ACCURACY = 8
    private val VOLUME_ACCURACY = 6

    private val handler: Handler

    private var bufferData = mutableListOf<SymbolDTO>()

    var data: MutableList<SymbolDTO> = mutableListOf()
    lateinit var activity: FragmentActivity
    var tickerDisposable: Disposable? = null
    var sortBy = ""
    var isScrolling = false
    var showFavoriteStars = true
    var onFavoriteBtnClick: ((Int) -> Unit)? = null
    var currency = ""
    var smallWidth = false
    var context: Context? = null
    var recycler : androidx.recyclerview.widget.RecyclerView? = null

    var isScreenVisible = currency == "BTC" || currency == ""

    fun subscribeToTicker() {

        tickerDisposable = RXCache
                .onTick
                .onBackpressureBuffer(10, {

                }, BackpressureOverflowStrategy.DROP_OLDEST)
                .observeOn(Schedulers.computation())
                .subscribe(
                        {
                            val tickers = it.filter {
                                data.any { symbolDto -> symbolDto?.symbol == it.symbol }
                            }
                            if (isScrolling) {
                                mergeData(tickers, bufferData)
                            } else {
                                tickersSnapshot(tickers)
                            }
                        },
                        {
                            Log.e("backpressure", it?.message ?: "something wrong")
                            reload()
                        }
                )
    }

    override fun onAttachedToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
    }

    init {
        val bgThread = HandlerThread("background")
        bgThread.start()
        handler = Handler(bgThread.looper)
    }

    fun isSmallWidthDisplay(): Boolean {
        try {
            var display = (context?.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
            var point = Point()
            display.getSize(point)
            return (point.x < 360)
        } catch (e: Exception) {
            return false
        }
    }

    fun provideContext(context: Context?) {
        this.context = context
        smallWidth = isSmallWidthDisplay()
    }

    override fun tickersSnapshot(tickers: List<TickerParams>) {
        if(isScreenVisible) {
            if (bufferData.isNotEmpty()) {
                for (symbol in bufferData) {
                    val id = data.indexOfFirst { symbol.symbol == it.symbol }
                    if (id != -1) {
                        data[id] = symbol
                    }
                }
                bufferData.clear()
            }

            mergeData(tickers, data)

            val clearedData = data.distinct()
            data.clear()
            when (sortBy) {
                "changeIncr" -> {
                    data.addAll(clearedData.sortedBy { it.change24 })
                }
                "changeDecr" -> {
                    data.addAll(clearedData.sortedByDescending { it.change24 })
                }
                else -> {
                    data.addAll(clearedData)
                }
            }

            Handler(Looper.getMainLooper()).post {
                recycler?.recycledViewPool?.clear()
                notifyDataSetChanged()
            }
        }
    }

    private fun mergeData(src: List<TickerParams>, dst: MutableList<SymbolDTO>) {
        for (ticker in src) {
            var index = -1
            for (i in 0 until dst.size) {
                if (dst[i].symbol == ticker.symbol) {
                    index = i
                    break
                }
            }

            val symbol = RXCache.getSymbol(ticker.symbol) ?: continue

            if (index < 0) {
                dst.add(RXCache.toDto(symbol))
            } else {
                dst[index] = RXCache.toDto(symbol)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val viewHolder = ViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(
                                R.layout.item_markets,
                                parent,
                                false
                        ),
                onClickListener
        )

        return viewHolder
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        with(holder.itemView) {
           //updateSymbolFromTicker(position, DataCache.getTicker(symbols[position].symbol))
            val symbol = data[position]

             val curr1 = symbol.base
           markets_curr.text = curr1

            val tickSize = RXCache.getSymbol(symbol.symbol)?.tickSize
            val accuracy = MIN_PRICE_ACCURACY

            markets_last_price.text = symbol.price.toString()

            val btcInfo = RXCache.getSymbol(symbol.base + "BTC")
            val btcPrice = symbol.price

                markets_last_price_usd.text = "$btcPrice BTC"
                markets_volume.text = symbol.volume.toString()


            markets_change.text = String.format(Locale.US, "%+.2f%%", symbol.change24)
            markets_change.setTextColor(resources.getColor(if (symbol.change24 >= 0) R.color.chart_green else R.color.orange_new))

            val favColor = resources.getColor(R.color.color_btn_favorite)
            btn_add_to_favorite.setColorFilter(favColor, PorterDuff.Mode.SRC_IN)
            if (smallWidth) btn_add_to_favorite.visibility = View.GONE
            btn_add_to_favorite.setOnClickListener {
                val pos = position
                if (pos != RecyclerView.NO_POSITION) {
                    onFavoriteBtnClick?.invoke(pos)
                }
            }

            tag = symbol.symbol
        }
    }

    fun updateSubscribtions(recycler: androidx.recyclerview.widget.RecyclerView): Boolean {/*
        var visibleItems: MutableList<String> = arrayListOf()

        for (i in 0 until recycler.layoutManager!!.childCount) {
            val child = recycler.layoutManager!!.getChildAt(i)

            if (child!!.tag != null && recycler.layoutManager!!.isViewPartiallyVisible(child, true, true)) visibleItems.add(child.tag as String)
        }

        var toSubscribe = visibleItems.minus(MarketsPresenter.subscriptions!!)
        var toUnscubscribe = MarketsPresenter.subscriptions!!.minus(visibleItems)

        toSubscribe.forEach { Subscribtions.subscribeTicker(it) }
        toUnscubscribe.forEach { Subscribtions.unsubscribeTicker(it) }

        *//*Log.d("SUB", toSubscribe.toString())
        Log.d("SUB", toUnscubscribe.toString())
        Log.d("SUB", MarketsPresenter.subscriptions!!.toString())*//*

        MarketsPresenter.subscriptions!!.clear()
        MarketsPresenter.subscriptions!!.addAll(visibleItems)

        return visibleItems.size != 0*/
        return true
    }

    class ViewHolder(view: View, listener: View.OnClickListener?) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener(listener)
        }
    }

//    class MarketsDiffUtils(val oldList: ArrayList<SymbolDTO>, val newList: ArrayList<SymbolDTO>) : DiffUtil.Callback() {
//
//        override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
//            return try {
//                val oldItem = oldList[oldItemPos]
//                val newItem = newList[newItemPos]
//
//                (oldItem.symbol == newItem.symbol)
//            }catch (e: java.lang.Exception){
//                false
//            }
//        }
//
//        override fun getOldListSize() = oldList.size
//
//        override fun getNewListSize() = newList.size
//
//        override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
//            val oldItem = oldList[oldItemPos]
//            val newItem = newList[newItemPos]
//
//            return ((oldItem.change24 == newItem.change24) && (oldItem.price == newItem.price) && (oldItem.volume == newItem.volume))
//        }
//    }
}