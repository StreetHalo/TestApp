package trade.paper.app.views


import android.text.InputFilter
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.*
import com.example.testapp.BaseFragment
import com.example.testapp.R
import trade.paper.app.models.hawk.Settings

class OrdersFilterWidget(var parent: BaseFragment, var listener : FilterListener) {

    data class FilterData(
            var pairFilter : String = "",
            var time : TimeUnits = TimeUnits.ALL,
            var type : TypeUnits = TypeUnits.BUYSELL
    )

    enum class TimeUnits{
        DAY,
        WEEK,
        MONTH,
        ALL,
        NONE
    }

    enum class TypeUnits(val str : String) {
        BUYSELL("buysell"),
        BUY("buy"),
        SELL("sell")
    }

    interface FilterListener
    {
        fun acceptFilter(data : FilterData)
        fun resetFilter()
    }

    companion object {
        const val ALL = "All"
    }

    var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_orders_filter, null, false)
    lateinit var accept: Button
    lateinit var decline: Button
    lateinit var radioDate: RadioGroup
    lateinit var radioType: RadioGroup
    lateinit var pairFilter: EditText
    var filterData = FilterData()

    init {
        setup()
    }

    fun setup(){
        accept = view.findViewById(R.id.fltr_accept)
        decline = view.findViewById(R.id.fltr_decline)
        radioDate = view.findViewById(R.id.period_layout)
        radioType = view.findViewById(R.id.radio_type)
        pairFilter = view.findViewById(R.id.pair_filter)
        onClickSetup()
        ptLocaleFontSetup()

        pairFilter.filters = pairFilter.filters + InputFilter.AllCaps()

        val metrics = parent.resources.displayMetrics
        val px : Int = (280 * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

        val layoutParams = LinearLayout.LayoutParams(px, LinearLayout.LayoutParams.MATCH_PARENT);
        view.layoutParams = layoutParams
    }

    fun ptLocaleFontSetup(){

    }

    fun resetDate() {
        radioDate.clearCheck()

        filterData.time = TimeUnits.NONE
    }

    fun onClickSetup(){
        accept.setOnClickListener {
            val pairFilter = pairFilter.text.toString()
            val time = if(radioDate.checkedRadioButtonId > 0)
                TimeUnits.values()[radioDate.indexOfChild(radioDate.findViewById(radioDate.checkedRadioButtonId))] else TimeUnits.NONE
            val type = TypeUnits.values()[radioType.indexOfChild(radioType.findViewById(radioType.checkedRadioButtonId))]

            filterData = FilterData(pairFilter, time, type)

            listener.acceptFilter(filterData)
        }
        decline.setOnClickListener {
            filterData = FilterData()

            radioDate.clearCheck()
            radioType.check(R.id.filter_buysell)
            pairFilter.setText("")

            listener.resetFilter()
            listener.acceptFilter(filterData)
        }
    }
}