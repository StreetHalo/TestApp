package trade.paper.app.dialogs

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.View
import com.example.testapp.R
import kotlinx.android.synthetic.main.dialog_orderbook_type.*
import trade.paper.app.models.cache.AppCache
import kotlinx.android.synthetic.main.dialog_orderbook_type.view.*

class OrderbookTypeSelectorDialog(
        var baseCurrency: String,
        var feeCurrency: String,
        val setAccuracyForLeftPartAmount: () -> Unit,
        val setAccuracyForLeftPartTotal: () -> Unit,
        val orderbookTypeChanged: () -> Unit) {
    var self : BottomSheetDialog? = null

    @SuppressLint("InflateParams")
    fun getDialog(context: Context): BottomSheetDialog {
        self = BottomSheetDialog(context).apply {
            setContentView(layoutInflater.inflate(R.layout.dialog_orderbook_type, null).apply {
                orderbook_type_selector_btn_first_currency.text = context.getString(R.string.orderbook_selector_sum, baseCurrency)
                orderbook_type_selector_btn_second_currency.text = context.getString(R.string.orderbook_selector_sum, feeCurrency)

                val itemClickListener = View.OnClickListener {
                    if (it.isSelected) return@OnClickListener
                    it.isSelected = !it.isSelected

                   /* AppCache.orderbookSelectorType = when(it.id) {
                        orderbook_type_selector_btn_amount.id -> {
                            setAccuracyForLeftPartAmount()
                            orderbook_type_selector_btn_first_currency.isSelected = false
                            orderbook_type_selector_btn_second_currency.isSelected = false
                            DetailOrderbook.OrderbookTypeEnum.AMOUNT
                        }
                        orderbook_type_selector_btn_first_currency.id -> {
                            setAccuracyForLeftPartAmount()
                            orderbook_type_selector_btn_amount.isSelected = false
                            orderbook_type_selector_btn_second_currency.isSelected = false
                            DetailOrderbook.OrderbookTypeEnum.SUM_BASE_CURRENCY
                        }
                        orderbook_type_selector_btn_second_currency.id -> {
                            setAccuracyForLeftPartTotal()
                            orderbook_type_selector_btn_amount.isSelected = false
                            orderbook_type_selector_btn_first_currency.isSelected = false
                            DetailOrderbook.OrderbookTypeEnum.SUM_FEE_CURRENCY
                        }
                        else -> DetailOrderbook.OrderbookTypeEnum.AMOUNT
                    }*/
                    self?.dismiss()

                    orderbookTypeChanged()
                }

                orderbook_type_selector_btn_amount.setOnClickListener(itemClickListener)
                orderbook_type_selector_btn_first_currency.setOnClickListener(itemClickListener)
                orderbook_type_selector_btn_second_currency.setOnClickListener(itemClickListener)

           /*     when(AppCache.orderbookSelectorType) {
                    DetailOrderbook.OrderbookTypeEnum.AMOUNT -> {
                        orderbook_type_selector_btn_amount.isSelected = true
                    }
                    DetailOrderbook.OrderbookTypeEnum.SUM_BASE_CURRENCY -> {
                        orderbook_type_selector_btn_first_currency.isSelected = true
                    }
                    DetailOrderbook.OrderbookTypeEnum.SUM_FEE_CURRENCY -> {
                        orderbook_type_selector_btn_second_currency.isSelected = true
                    }
                }*/
            })
        }
        return self!!
    }

}