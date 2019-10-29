package trade.paper.app.adapters

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.testapp.R
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.rpc.params.AskBid
import kotlinx.android.synthetic.main.item_ask_bid.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil

class AskBidRecyclerAdapter(
        var type: Type,
        var onClickListener: View.OnClickListener = View.OnClickListener { }
) : androidx.recyclerview.widget.RecyclerView.Adapter<AskBidRecyclerAdapter.ViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        var MAX_ORDERS_COUNT: Int = 1000
    }

    var data = MutableList(MAX_ORDERS_COUNT) { AskBid(BigDecimal(0.0), 0.0) }
    var sumData = data.sumList()
    var accuracy = 0

    var spaceBeforeDotForAmount = 0f
    var spaceAfterDotForAmount = 0f
    var numbersAfterDotAmount = 0
    var numbersAfterDotAmountForFirstItem = 0

    var numbersAfterDotPrice = 0
    var numbersAfterDotPriceForFirstItem = 0

    var stripFractionAmount = false
    var boldOrders = hashSetOf<BigDecimal>()
    var symbol = ""
    var cachedWidth = 0
    var maxItems = MAX_ORDERS_COUNT

    var canUpdate = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater
                        .from(parent.context!!)
                        .inflate(
                                when (type) {
                                    Type.ASK -> R.layout.item_ask_detail
                                    Type.BID -> R.layout.item_bid_detail
                                    else -> R.layout.item_ask_bid
                                },
                                parent,
                                false
                        ).apply {
                        setOnClickListener(onClickListener)
                    }
        )
    }

    @Synchronized fun setOrdersData(orderData: List<AskBid>, bestAskBid: AskBid? = null) {
        if (!canUpdate) return
        data.clear()

        if (bestAskBid != null) data.add(bestAskBid)

        // Drop first item because bestAskBid is a first item and we add it to data already
        if (orderData.isNotEmpty()&&bestAskBid == orderData[0]) data.addAll(orderData.drop(1).take(maxItems))
        else data.addAll(orderData.take(maxItems))

        boldOrders.clear()

        if(type == Type.ASK_TRADES) data.reverse()

        sumData = data.sumList(type == Type.ASK_TRADES)

        numbersAfterDotPrice = 0
        spaceBeforeDotForAmount = 0f
        spaceAfterDotForAmount = 0f
        stripFractionAmount = true

        data.forEachIndexed { index, askbid ->
            val scalePrice: Int

          /*  scalePrice = if (type == Type.ASK || type == Type.BID) {
            //    val price = Formatter.amountLong(askbid.price.toDouble())
           //     scale(price)
            } else {
                askbid.price.scale()
            }*/

            if (index != 0) {
              //  numbersAfterDotPrice = Math.max(numbersAfterDotPrice, scalePrice)

                if (askbid.size < 10.0) stripFractionAmount = false
            }

          //  spaceBeforeDotForAmount = Math.max(spaceBeforeDotForAmount, spaceBeforeDot(Formatter.amountSumOrderbook(askbid.size, 0))) // 0 because it's does not matter
           // spaceAfterDotForAmount = Math.max(spaceAfterDotForAmount, spaceAfterDot(Formatter.amountSumOrderbook(askbid.size, numbersAfterDotAmount))) // 0 because it's does not matter
        }

        if(symbol.isNotEmpty()) {
  /*          boldOrders.addAll(RXCache.getOrdersForSymbol(symbol).map{
               // val value = OrderbookCalculation.calculatePriceFromAccuracy(BigDecimal(it.price), accuracy, RoundingMode.DOWN)

                // Quickfix
              //  if (Formatter.amountRoundUpTo13(value) == "0.0") {
               //     return@map OrderbookCalculation.calculatePriceFromAccuracy(BigDecimal(it.price), accuracy, RoundingMode.UP)
             //   }

                return@map value
            })*/
        }

        handler.post {
            notifyItemRangeChanged(0, data.size)
        }
    }

    fun scale(value : String) : Int {
        if(value.endsWith(".0")) return 0

        val dotIndex = value.indexOf('.')
        if(dotIndex == -1) return 0

        return value.substring(dotIndex).length - 1
    }

    val tPaint: Paint by lazy {
        var paint = Paint()
        val spTextSize = 14f

        val textSize = spTextSize
        paint.textSize = textSize
        paint.typeface = Typeface.MONOSPACE

        return@lazy paint
    }

    private fun spaceBeforeDot(value: String) : Float {
        return try {
            if(value.contains("."))
                tPaint.measureText(value.take(value.indexOf(".")))
            else
                tPaint.measureText(value)

        } catch (e: Exception) {
            if(value.contains("."))
                tPaint.measureText("0")
            else
                tPaint.measureText(value)
        }
    }

    private fun spaceAfterDot(value: String) : Float {
        return try {
            if(value.contains("."))
                tPaint.measureText(value.substring(value.indexOf(".")))
            else
                0f
        } catch (e: Exception) {
            if(value.contains("."))
                tPaint.measureText(value) - tPaint.measureText("0")
            else
                0f
        }
    }

    fun getLeftSpacesForValue(value: String) : Float {
        return spaceBeforeDotForAmount - spaceBeforeDot(value)
    }

    fun getRightSpacesForValue(value: String) : Float {
        return spaceAfterDotForAmount - spaceAfterDot(value)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {

            val item_amount: TextView = this.findViewById(R.id.item_amount)
            val item_price: TextView = this.findViewById(R.id.item_price)

            val amount = data[position].size
            val price = data[position].price
            tag = price

            if (type == Type.BID_TRADES || type == Type.ASK_TRADES)
                item_price.setTextColor(ContextCompat.getColor(context, if (type == Type.BID_TRADES) R.color.green else R.color.red))

            if(amount == 0.0 || price.toDouble() == 0.0) {
                item_amount.text = "--"
                item_price.text = "--"
                back_color.visibility = View.GONE
                item_amount.setPadding(0, 0, 0, 0)
            } else {
                if(position == 0) {
                 //   item_amount.text = Formatter.amountSumOrderbook(amount, numbersAfterDotAmountForFirstItem)//if(!stripFractionAmount) numbersAfterDotAmountForFirstItem else 0)
                //    item_price.text = Formatter.amountWithAccuracy(price, numbersAfterDotPriceForFirstItem)
                } else {
                 //   item_amount.text = Formatter.amountSumOrderbook(amount, numbersAfterDotAmount)//if(!stripFractionAmount) numbersAfterDotAmount else 0)
                //    item_price.text = Formatter.amountWithAccuracy(price, numbersAfterDotPrice)
                }
              //  val amountText = Formatter.amountSumOrderbook(amount, numbersAfterDotAmount)//if(!stripFractionAmount) numbersAfterDotAmount else 0)

                if(type == Type.BID)
               //     item_amount.setPadding(ceil(getLeftSpacesForValue(amountText).toDouble()).toInt(), 0, 0, 0)
                else if (type == Type.ASK)
                //    item_amount.setPadding(0, 0, ceil(getRightSpacesForValue(amountText).toDouble()).toInt(), 0)

              //  item_amount.text = amountText
                if(cachedWidth == 0) back_color.visibility = View.GONE else back_color.visibility = View.VISIBLE
            }

            val isBoldOrder = boldOrders.contains(price)

            //Log.d("DebugViewOrders", boldOrders.joinToString { Formatter.amountRoundUpTo13(it) })
            //Log.d("DebugView", "$isBoldOrder ${Formatter.amountRoundUpTo13(price)} $accuracy")

            item_amount.setTypeface(Typeface.MONOSPACE, if(isBoldOrder) Typeface.BOLD else Typeface.NORMAL)
            item_price.setTypeface(null, if(isBoldOrder) Typeface.BOLD else Typeface.NORMAL)
            (this as FrameLayout).foreground = if (isBoldOrder) ColorDrawable(ContextCompat.getColor(context, R.color.light_blue)) else null

            val width = cachedWidth
            val percent = if(position < sumData.size) sumData[position] else 0.0
            val backColorWidth = (width * (1 - percent)).toInt()
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            params.setMargins(if(type != Type.BID) 0 else backColorWidth, 0, if(type == Type.BID) 0 else backColorWidth, 0)
            back_color.layoutParams = params

            if(type == Type.BID_TRADES || type == Type.BID) {
                back_color.setBackgroundColor(ContextCompat.getColor(context, R.color.green_transparent_dark_new))// if(position % 2 == 0) R.color.green_transparent_dark else R.color.green_transparent_dark2))
            }
            else if(type == Type.ASK_TRADES || type == Type.ASK) {
                back_color.setBackgroundColor(ContextCompat.getColor(context, R.color.red_transparent_dark_new))//if(position % 2 == 0) R.color.red_transparent_dark else R.color.red_transparent_dark2))
            }

            back_color.invalidate()
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)

    enum class Type {
        ASK, BID, ASK_TRADES, BID_TRADES
    }

    private fun List<AskBid>.sumList(reversed: Boolean = false) : List<Double> {
        val destination = arrayListOf<Double>()
        var sum = 0.0

        for (item in this) {
            if (item.size == 0.0 || item.price.toDouble() == 0.0) continue

            sum += item.size
            destination.add(sum)
        }

        if(sum == 0.0) return destination

        if(reversed) {
            destination.reverse()
        }

        return destination.map { it / sum }
    }
}