package trade.paper.app.adapters

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.testapp.R
import trade.paper.app.models.rest.ActiveOrdersResponse
import trade.paper.app.models.rpc.params.AskBid
import trade.paper.app.utils.extensions.gone
import kotlinx.android.synthetic.main.item_orderbook.view.*
import java.math.BigDecimal
import java.math.RoundingMode


class OrderbookRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ASKBID = R.layout.item_orderbook
    private val VIEW_TYPE_CENTER = 1

    private val handler = Handler(Looper.getMainLooper())

    private var askData = ArrayList<AskBid>()
    private var bidData = ArrayList<AskBid>()

    private val askSums = ArrayList<Double>()
    private val bidSums = ArrayList<Double>()

    var numbersAfterDotAmount = 0
    var numbersAfterDotPrice = 0
    var numbersAfterDotPriceForBest = 0
    var accuracy = 1

    var isScrolling = false

    private var lastTradePrice = ""
    private var lastTradePriceColor = R.color.gray_light

    private var myOrders = ArrayList<ActiveOrdersResponse>()

    fun removeOrder(id: String){
        val i = myOrders.iterator()
        while (i.hasNext()){
            if(i.next().clientOrderId==id)
                i.remove()
        }
    }

    var viewWidth = 0

    var onItemClick: ((BigDecimal) -> Unit)? = null

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = if (viewType == VIEW_TYPE_ASKBID)
            OrderbookAskBidViewHolder(LayoutInflater.from(container.context).inflate(viewType, container, false))
        else
            OrderbookCenterViewHolder(createCenterTextView(container.context))
        return viewHolder
    }

    override fun getItemViewType(position: Int) = if (position == askData.size) VIEW_TYPE_CENTER else VIEW_TYPE_ASKBID

    override fun getItemCount() = askData.size + bidData.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val color: Int
        val percent: Double
        val item = when {
            position < askData.size -> {
                percent = try {
                    askSums[position] / askSums.first()
                } catch (e: Exception) {
                    0.0
                }
                color = R.color.rd_red
                askData[position]
            }
            position > askData.size -> {
                val askPosition = position - askData.size - 1

                percent = try {
                    bidSums[askPosition - 1] / bidSums.last()
                } catch (e: Exception) {
                    0.0
                }
                color = R.color.rd_green
                bidData[askPosition]
            }
            else -> {
                (holder.itemView as TextView).apply {
                    text = lastTradePrice
                    setTextColor(resources.getColor(lastTradePriceColor))
                }
                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(BigDecimal(lastTradePrice))
                }
                return
            }
        }
        val price = if (position == askData.size - 1 || position == askData.size + 1) {
          //  Formatter.amountWithAccuracy(item.price, numbersAfterDotPriceForBest)
        } else {
          //  Formatter.amountWithAccuracy(item.price, numbersAfterDotPrice)
        }
       // val amount = Formatter.amountSumOrderbook(item.size, numbersAfterDotAmount)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item.price)
        }

        var isBold = false
        myOrders.forEach {
            if (item.price == BigDecimal(-999.0)) return@forEach

            val bPrice = BigDecimal(it.price)
            val roundingMode = if(position >= askData.size - 1) RoundingMode.UP else RoundingMode.DOWN
        //    var accuracyPrice = OrderbookCalculation.calculatePriceFromAccuracy(bPrice, accuracy, roundingMode)

        /*    if (bPrice == item.price || accuracyPrice == item.price)
                isBold = true*/

            //Log.d("DebugView", "$roundingMode $isBold ${Formatter.amountRoundUpTo13(bPrice)} ${Formatter.amountRoundUpTo13(accuracyPrice)} ${Formatter.amountRoundUpTo13(item.price)}")
        }

      //  var isEmpty = item is ExchangeMainPresenter.EmptyAskBid

      //  (holder as OrderbookAskBidViewHolder).bind(price, amount, percent, color, viewWidth, isBold, isEmpty)
    }

    fun setItems(askData: List<AskBid>, bidData: List<AskBid>): Boolean {
        val isFirstData = (this.askData.isEmpty() && this.bidData.isEmpty())

        this.askData = ArrayList(askData.reversed())

        this.bidData = ArrayList(bidData)

        askSums.clear()
        bidSums.clear()

        numbersAfterDotPrice = 0

        askData.forEachIndexed { index, it ->
            if (index == 0) {
            } else {
                askSums.add((askSums.lastOrNull() ?: 0.0) + it.size)
                numbersAfterDotPrice = Math.max(numbersAfterDotPrice, it.price.scale())
            }
        }
        askSums.reverse()

        bidData.forEachIndexed { index, it ->
            if (index == 0) {
            } else {
                bidSums.add((bidSums.lastOrNull() ?: 0.0) + it.size)

                numbersAfterDotPrice = Math.max(numbersAfterDotPrice, it.price.scale())
            }
        }

        Log.d("SCORLLING", "${isScrolling}")
        if (!isScrolling) handler.post { notifyDataSetChanged() }

        return isFirstData
    }

    fun getCenterPosition() = askData.size

    fun setLastTradePrice(price: String, color: Int) {
        lastTradePrice = price
        lastTradePriceColor = color
        notifyItemChanged(askData.size)
    }

    private fun scale(value: String): Int {
        if (value.endsWith(".0")) return 0

        val dotIndex = value.indexOf('.')
        if (dotIndex == -1) return 0

        return value.substring(dotIndex).length - 1
    }

    private fun createCenterTextView(context: Context): TextView {
        return TextView(context).apply {
            val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28f, context.resources.displayMetrics).toInt()
            layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setBackgroundResource(R.drawable.ripple_color_dark_gray_to_plane_blue)
        }
    }

    fun setMyOrders(orders: List<ActiveOrdersResponse>) {
        myOrders = ArrayList(orders)
        notifyDataSetChanged()
    }
}

class OrderbookCenterViewHolder(v: View) : RecyclerView.ViewHolder(v)

class OrderbookAskBidViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    fun bind(price: String, amount: String, percent: Double, color: Int, viewWidth: Int, isBold: Boolean, isEmtpty: Boolean) {
        if(isEmtpty){
            with(itemView){
                tv_price_item_orderbook.text=""
                tv_amount_item_orderbook.text = ""
               // background_item_orderbook.setBackgroundResource(R.color.light_blue)
                background_item_orderbook.layoutParams = FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
              //  orderbook_separator.gone()
                this.isClickable = false
            }
        }
        else{
            with(itemView) {
                tv_price_item_orderbook.text = price
                tv_price_item_orderbook.setTextColor(ContextCompat.getColor(context, color))
                tv_amount_item_orderbook.text = amount

                background_item_orderbook.setBackgroundResource(color)
                background_item_orderbook.layoutParams = FrameLayout.LayoutParams((viewWidth * percent).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)

                if (isBold) {
                    (this as FrameLayout).foreground = ColorDrawable(ContextCompat.getColor(context, R.color.white_transparent))
                    tv_amount_item_orderbook.setTypeface(null, Typeface.BOLD)
                    tv_price_item_orderbook.setTypeface(null, Typeface.BOLD)
                } else {
                    (this as FrameLayout).foreground = null
                    tv_amount_item_orderbook.setTypeface(null, Typeface.NORMAL)
                    tv_price_item_orderbook.setTypeface(null, Typeface.NORMAL)
                }
            }
        }
    }
}