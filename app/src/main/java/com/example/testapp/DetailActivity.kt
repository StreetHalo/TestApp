package com.example.testapp

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.DrawableUtils
import com.example.testapp.ui.dashboard.ExchangeActivity
import com.example.testapp.ui.dashboard.MarketsActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hittechsexpertlimited.hitbtc.fragments.detail.chartIndicatorsModels.ChartIndicatorsSettings
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.dialog_chart_settings_indicators.view.*
import kotlinx.android.synthetic.main.dialog_chart_settings_periods.view.*
import kotlinx.android.synthetic.main.fragment_symbol_detail.*
import kotlinx.android.synthetic.main.fragment_symbol_detail.btn_chart_settings_candles
import kotlinx.android.synthetic.main.fragment_symbol_detail.btn_chart_settings_depth
import kotlinx.android.synthetic.main.fragment_symbol_detail.btn_chart_settings_indicatiors
import kotlinx.android.synthetic.main.fragment_symbol_detail.btn_chart_settings_line
import kotlinx.android.synthetic.main.fragment_symbol_detail.btn_chart_settings_period
import kotlinx.android.synthetic.main.fragment_symbol_detail.detail_symbol_buy
import kotlinx.android.synthetic.main.fragment_symbol_detail.detail_symbol_sell
import kotlinx.android.synthetic.main.popup_chart_depth_detail.view.*
import kotlinx.android.synthetic.main.popup_fast_scroll.view.*
import org.w3c.dom.Text
import trade.paper.app.dialogs.IndicatorsDialogFragment
import trade.paper.app.utils.extensions.disableScroll
import trade.paper.app.utils.extensions.visible

class DetailActivity : AppCompatActivity() {
    private lateinit var chartDetailPointPopup: PopupWindow
    private lateinit var chartDepthDetailPointPopup: PopupWindow
    private lateinit var chartSettingsPeriodDialog: BottomSheetDialog
    private lateinit var chartSettingsIndicatorsDialog: BottomSheetDialog
    private var indicatorDialog = IndicatorsDialogFragment()
    private var indicatorsSettings = ChartIndicatorsSettings()
    private var fastScrollPopup: PopupWindow? = null
    var spinnerItems = mutableListOf(
        "m1",
        "m3",
        "m5",
        "m15",
        "m30",
        "H1",
        "H4",
        "D1",
        "W1",
        "M1"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initChartSettingsIndicatorsDialog()
        initChartDetailPointPopup()
        initFastScrollPopup()
        initChartSettingsPeriodDialog()
        initChartSettingsIndicatorsDialog()
        initChartSettingsButtons()

        val detail_symbol_back = findViewById<TextView>(R.id.detail_symbol_back)


        detail_symbol_back.setOnClickListener {
            onBackPressed()
        }
        detail_symbol_buy.setOnClickListener {
            val notificationIntent = Intent(this, ExchangeActivity::class.java)
            startActivity(notificationIntent)
          //  finish()
        }

        detail_symbol_sell.setOnClickListener {
            val notificationIntent = Intent(this, ExchangeActivity::class.java)
            startActivity(notificationIntent)
        }

    }

    private fun ImageButton.selected(isSelected: Boolean) {
        this.isSelected = isSelected
        this.setColorFilter(
            resources.getColor(
                if (isSelected) R.color.colorAccent
                else R.color.color_settings_selected_false
            ), PorterDuff.Mode.SRC_IN
        )
    }

    private fun initChartSettingsButtons() {

        btn_chart_settings_candles.selected(false)
        btn_chart_settings_depth.selected(false)
        btn_chart_settings_line.selected(false)
        /*  when (chartType) {
            ChartType.Candles -> {
                btn_chart_settings_indicatiors.alpha = 1f
                btn_chart_settings_period.alpha = 1f
                btn_chart_settings_candles.selected(true)
            }
            ChartType.Lines -> {
                btn_chart_settings_indicatiors.alpha = 1f
                btn_chart_settings_period.alpha = 1f
                btn_chart_settings_line.selected(true)
            }
            else -> {
                btn_chart_settings_indicatiors.alpha = 0.5f
                btn_chart_settings_period.alpha = 0.5f
                btn_chart_settings_depth.selected(true)
            }
        }*/

        btn_chart_settings_candles.setOnClickListener {
            /*     btn_chart_settings_period.isClickable = true
            btn_chart_settings_indicatiors.isClickable = true
            if (!it.isSelected) {

                btn_chart_settings_period.alpha = 1f
                (it as ImageButton).selected(true)
                btn_chart_settings_depth.selected(false)
                btn_chart_settings_line.selected(false)


                val fragment = childFragmentManager.findFragmentByTag(candlesChart::class.java.simpleName)
                if (fragment==null){
                    changeChart(candlesChart)
                }
                currentVisibleChartType = ChartType.Candles
                chartType = ChartType.Candles
                chartSettingsListener?.candlesChartSelected()
                hideChartDetailsPopup()
                storedCandle = null
            }
            hidePopUp()

            if (btn_chart_settings_period.text.toString().equals(getString(R.string.line), true)) {
                btn_chart_settings_indicatiors.alpha = 0.5f
            } else {
                btn_chart_settings_indicatiors.alpha = 1f
            }*/
        }
        btn_chart_settings_depth.setOnClickListener {
            /*  btn_chart_settings_period.isClickable = false
            btn_chart_settings_indicatiors.isClickable = false
            if (!it.isSelected) {
                fastScrollPopup?.dismiss()
                (it as ImageButton).selected(true)
                btn_chart_settings_candles.selected(false)
                btn_chart_settings_line.selected(false)
                AnalyticsManager.sendEvent(AnalyticsManager.Event.SymbolDetailGraphicTab, if (parent == MarketsFragment::class.simpleName) "Markets_" else "Trades_", "Depth")

                currentVisibleChartType = ChartType.Depth
                chartType = ChartType.Depth
                changeChart(depthChart)
                hideChartDetailsPopup()
                storedCandle = null*/
        }
        //  hidePopUp()
        btn_chart_settings_period.alpha = 0.5f
        btn_chart_settings_indicatiors.alpha = 0.5f


        btn_chart_settings_line.setOnClickListener {
            /*        btn_chart_settings_period.isClickable = true
            btn_chart_settings_indicatiors.isClickable = true
            if(!it.isSelected){
                btn_chart_settings_indicatiors.alpha = 1f
                btn_chart_settings_period.alpha = 1f
                (it as ImageButton).selected(true)
                btn_chart_settings_candles.selected(false)
                btn_chart_settings_depth.selected(false)

                currentVisibleChartType = ChartType.Lines
                chartType = ChartType.Lines
                val fragment = childFragmentManager.findFragmentByTag(candlesChart::class.java.simpleName)
                if (fragment==null){
                    changeChart(candlesChart)
                }
                chartSettingsListener?.lineChartSelected()
                hideChartDetailsPopup()
                storedCandle = null*/
        }




        btn_chart_settings_period.text = "???"
        btn_chart_settings_period.setOnClickListener {
            if (!btn_chart_settings_depth.isSelected) {
                chartSettingsPeriodDialog.show()
             //   hideChartDetailsPopup()
            }
        }
        btn_chart_settings_indicatiors.setOnClickListener {
           // AnalyticsManager.sendEvent(AnalyticsManager.Event.SymbolDetailIndicatorsClicked)
            if (!btn_chart_settings_depth.isSelected && !(btn_chart_settings_period.text.toString().equals("line", true))) {
             //   indicatorDialog.show(fragmentManager, "Indicator")
            }
        }
    }


    private fun initChartSettingsPeriodDialog() {
        chartSettingsPeriodDialog = BottomSheetDialog(this).apply {
            val layout = layoutInflater.inflate(R.layout.dialog_chart_settings_periods, null)
            setContentView(layout)
            val margin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12f,
                resources.displayMetrics
            ).toInt()
            val height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40f,
                resources.displayMetrics
            ).toInt()
            val items = ArrayList<TextView>()
            layout.apply {
                for (i in 0 until 5) {
                    for (j in 0 until 2) {
                        createChartSettingsPeriodItem(14f, spinnerItems[i + 5 * j], height, if (i != 4) margin else 0).apply {
                            if (j == 0) layout.top_settings.addView(this)
                            else layout.bottom_settings.addView(this)
                            items.add(this)

                            setOnClickListener {

                                fastScrollPopup?.dismiss()
                              //  candlesChart.topChart.shouldShowRightLine = false

/*
                                if (currentVisibleChartType == ChartType.Depth){
                                    chartSettingsPeriodDialog.dismiss()
                                    return@setOnClickListener
                                }
                                if (it.isSelected) {
                                    chartSettingsPeriodDialog.dismiss()
                                    return@setOnClickListener
                                }
                                this@SymbolDetailFragment.btn_chart_settings_period.text = this.text
                                period = this.text.toString()
                                for (child in items) {
                                    child.isSelected = child == this
                                }
                                AnalyticsManager.sendEvent(AnalyticsManager.Event.SymbolDetailGraphicTab, if (this@SymbolDetailFragment.parent == MarketsFragment::class.simpleName) "Markets_" else "Trades_", (it as TextView).text)

                                (this@SymbolDetailFragment.btn_chart_settings_indicatiors).visible()
                                val f = childFragmentManager.findFragmentByTag(candlesChart::class.java.simpleName)
                                periods.add(it.period())
                                if (f != null && f.isVisible) candlesChart.changePeriod(it.period())//spinner.period())
                                else {
                                    val p = it.period()
                                    changeChart(candlesChart as ChartsFragment)
                                }*/

                              //  chartSettingsPeriodDialog.dismiss()

                             //   indicatorDialog = IndicatorsDialogFragment()

                            }
                        }
                    }
                }
            }
        }
    }


    private fun createChartSettingsPeriodItem(txtSize: Float, txt: String, height: Int, margin: Int): TextView {
        return TextView(this).apply {
            text = txt
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, txtSize)
            layoutParams = LinearLayout.LayoutParams(0, height).apply {
                setMargins(0, 0, margin, 0)
                weight = 1f
            }
            background = resources.getDrawable(R.drawable.btn_chart_settings_period_background)
            gravity = Gravity.CENTER

                isSelected = true

        }
    }
    private fun initChartDetailPointPopup() {
        var popupView = LayoutInflater.from(this).inflate(R.layout.popup_chart_detail, null)
        chartDetailPointPopup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false)

        popupView = LayoutInflater.from(this).inflate(R.layout.popup_chart_depth_detail, null)
        chartDepthDetailPointPopup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false)
        with(popupView) {
            tv_chart_detail_popup_v1.text = Html.fromHtml(formatChartDetailValue("V1", "0.0"))
            tv_chart_detail_popup_v2.text = Html.fromHtml(formatChartDetailValue("V2", "0.0"))
            tv_chart_detail_popup_price.text = Html.fromHtml(formatChartDetailValue("P", "0.0"))
        }
    }

    private fun initFastScrollPopup() {
        var popupView = LayoutInflater.from(this).inflate(R.layout.popup_fast_scroll, null)
        fastScrollPopup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false)
        fastScrollPopup?.contentView?.apply {
            this.fast_scroll_btn.setOnClickListener {
                //candlesChart.scrollToMax()
                fastScrollPopup?.dismiss()

            }
        }
    }
    private fun initChartSettingsIndicatorsDialog() {
        chartSettingsIndicatorsDialog = BottomSheetDialog(this).apply {
            setContentView(layoutInflater.inflate(R.layout.dialog_chart_settings_indicators, null).apply {

              /*  when (chartSettings.bottomChart) {
                    ChartSettings.MACD -> {
                        btn_macd.isSelected = true
                    }
                    ChartSettings.BAR_CHART -> {
                        btn_volume.isSelected = true
                    }
                    ChartSettings.RSI -> {
                        btn_rsi.isSelected = true
                    }
                }*/

            //    btn_ma.isSelected = chartSettings.maState
             //   btn_ema.isSelected = chartSettings.emaState
           //     btn_boll.isSelected = chartSettings.bbState

                val topClickListener = View.OnClickListener {
                    if (it.isEnabled) {
                        it.isSelected = !it.isSelected
                    }
                //    saveIndicatorSettings()
                }

                val bottomClickListener = View.OnClickListener {
                    if (it.isSelected) return@OnClickListener
                    it.isSelected = !it.isSelected
                    val bottomChart = when (it.id) {
                        btn_volume.id -> {
                            btn_macd.isSelected = false
                            btn_rsi.isSelected = false
                        //    ChartSettings.BAR_CHART
                        }
                        btn_macd.id -> {
                            btn_volume.isSelected = false
                            btn_rsi.isSelected = false
                        //    ChartSettings.MACD
                        }
                        btn_rsi.id -> {
                            btn_volume.isSelected = false
                            btn_macd.isSelected = false
                          //  ChartSettings.RSI
                        }
                        else -> {
                          //  ChartSettings.BAR_CHART
                        }
                    }
                   // chartSettings.bottomChart = bottomChart
                   // saveIndicatorSettings()
                }

                btn_ma.setOnClickListener(topClickListener)
                btn_ema.setOnClickListener(topClickListener)
                btn_boll.setOnClickListener(topClickListener)
                btn_volume.setOnClickListener(bottomClickListener)
                btn_macd.setOnClickListener(bottomClickListener)
                btn_rsi.setOnClickListener(bottomClickListener)
            })
        }
        chartSettingsIndicatorsDialog.disableScroll()
    }

    private fun formatChartDetailValue(name: String, value: String) =
        "<font color=#80BECFDD>$name </font> <font color=#BECFDD>${value}</font>"

}
