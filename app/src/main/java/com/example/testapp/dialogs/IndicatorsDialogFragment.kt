package trade.paper.app.dialogs

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.testapp.DetailActivity
import com.example.testapp.R
import com.hittechsexpertlimited.hitbtc.fragments.detail.chartIndicatorsModels.*
import kotlinx.android.synthetic.main.layout_indicators.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trade.paper.app.utils.extensions.gone
import trade.paper.app.utils.extensions.invisible
import trade.paper.app.utils.extensions.visible
import kotlinx.android.synthetic.main.item_bottom_indicator.view.*
import kotlinx.android.synthetic.main.item_top_indicator.view.*
import kotlinx.android.synthetic.main.item_top_indicator.view.tv_source
import kotlinx.android.synthetic.main.layout_indicators.*
import kotlinx.android.synthetic.main.popup_indicator.view.*


class IndicatorsDialogFragment : DialogFragment() {
    private var expandedItem = ExpandedItem()
    private var chartIndicatorsSettings: ChartIndicatorsSettings = ChartIndicatorsSettings()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_indicators, container, false)
    }

    override fun onResume() {
        super.onResume()
        bindViews()
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    fun hideKeyboardFrom() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    fun bindViews() {
        bindRadioListeners()
        bindExpandListeners()
        bindButton()
        bindSpecialExpandView()
    }

    fun onBottomIndicatorClick(indicator: Indicators) {
        setRadioSelection(indicator)
    }

    fun onExpandClick(dropdownImage: ImageButton, indicator: Indicators) {
        var isExpanded = if (expandedItem.indicator == indicator) expandedItem.isExpanded else false
        if (expandedItem.indicator != indicator && expandedItem.isExpanded)
            expandedItem.imageButton?.animate()?.rotation(0f)?.duration = 100L

        dropdownImage.animate()?.rotation(if (!isExpanded) 180f else 0f)?.duration = 100L

        expandedItem.isExpanded = indicator != expandedItem.indicator || !isExpanded
        expandedItem.imageButton = dropdownImage

        closeAllExpandedItems()

        if(expandedItem.isExpanded) {
            when (indicator) {
                Indicators.MA -> {
                    ma_settings.visible()
                    ma_layout.setBackgroundColor(context?.resources?.getColor(R.color.rd_superdark_gray)!!)
                    line_ma_top.invisible()
                }
                Indicators.EMA -> {
                    ema_settings.visible()
                    ema_layout.setBackgroundColor(context?.resources?.getColor(R.color.rd_superdark_gray)!!)
                }
                Indicators.BB -> {
                    bb_settings.visible()
                    bb_layout.setBackgroundColor(context?.resources?.getColor(R.color.rd_superdark_gray)!!)
                }
                Indicators.VOL -> {
                    vol_settings.visible()
                    vol_layout.setBackgroundColor(context?.resources?.getColor(R.color.rd_superdark_gray)!!)
                }
                Indicators.MACD -> {
                    macd_settings.visible()
                    macd_layout.setBackgroundColor(context?.resources?.getColor(R.color.rd_superdark_gray)!!)
                }
                Indicators.RSI -> {
                    rsi_settings.visible()
                    rsi_layout.setBackgroundColor(context?.resources?.getColor(R.color.rd_superdark_gray)!!)
                }
            }
        }

        expandedItem.indicator = indicator
    }

    fun closeAllExpandedItems() {
        hideKeyboardFrom()
        ma_settings.gone()
        ma_layout.setBackgroundColor(context?.resources?.getColor(R.color.colorPrimaryDark)!!)

        ema_settings.gone()
        ema_layout.setBackgroundColor(context?.resources?.getColor(R.color.colorPrimaryDark)!!)

        bb_settings.gone()
        bb_layout.setBackgroundColor(context?.resources?.getColor(R.color.colorPrimaryDark)!!)

        vol_settings.gone()
        vol_layout.setBackgroundColor(context?.resources?.getColor(R.color.colorPrimaryDark)!!)

        macd_settings.gone()
        macd_layout.setBackgroundColor(context?.resources?.getColor(R.color.colorPrimaryDark)!!)

        rsi_settings.gone()
        rsi_layout.setBackgroundColor(context?.resources?.getColor(R.color.colorPrimaryDark)!!)
    }

    enum class Indicators {
        MA, EMA, BB,
        VOL, MACD, RSI
    }


    fun setRadioSelection(bottomIndicator: Indicators) {
        when (bottomIndicator) {
            Indicators.VOL -> {
                macd_checkbox.isChecked = false
                rsi_checkbox.isChecked = false
            }
            Indicators.RSI -> {
                vol_checkbox.isChecked = false
                macd_checkbox.isChecked = false
            }
            Indicators.MACD -> {
                vol_checkbox.isChecked = false
                rsi_checkbox.isChecked = false
            }
        }
    }

    fun bindRadioListeners() {
        vol_checkbox.setOnClickListener { onBottomIndicatorClick(Indicators.VOL) }
        rsi_checkbox.setOnClickListener { onBottomIndicatorClick(Indicators.RSI) }
        macd_checkbox.setOnClickListener { onBottomIndicatorClick(Indicators.MACD) }
    }

    fun bindExpandListeners() {
        ma_layout.setOnClickListener { onExpandClick(expand_ma, Indicators.MA) }
        ema_layout.setOnClickListener { onExpandClick(expand_ema, Indicators.EMA) }
        bb_layout.setOnClickListener { onExpandClick(expand_bb, Indicators.BB) }

        vol_layout.setOnClickListener { onExpandClick(btn_expand_vol, Indicators.VOL) }
        macd_layout.setOnClickListener { onExpandClick(macd_expand_btn, Indicators.MACD) }
        rsi_layout.setOnClickListener { onExpandClick(rsi_expand_btn, Indicators.RSI) }
    }

    fun getTopTextChangeListener(hintView: TextView, length: EditText?, source: TextView?, offset: EditText): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0?.trim().toString() != "") {
                    generateTopHint(hintView, length, source, offset)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        }
    }

    fun generateTopHint(hintView: TextView, length: EditText?, source: TextView?, offset: EditText) {
        val builder = StringBuilder("(")
        if (length != null)
            builder.append(length.text.toString() + ", ")

        if (source != null)
            builder.append(source.text.toString() + ", ")

        builder.append(offset.text.toString() + ")")

        hintView.text = builder.toString()
    }

    fun getBottomTextChangeListener(hintView: TextView, fast: EditText, show: EditText?, source: TextView?, signal: EditText?): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0?.trim().toString() != "") {
                    generateBottomHint(hintView, fast, show, source, signal)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        }
    }

    fun generateBottomHint(hintView: TextView, fast: EditText, show: EditText?, source: TextView?, signal: EditText?) {
        val builder = StringBuilder("(" + fast.text.toString())
        if (show != null)
            builder.append(", " + show.text.toString() + ", ")

        if (source != null)
            builder.append(source.text.toString() + ", ")

        if (signal != null)
            builder.append(signal.text.toString())

        builder.append(")")

        hintView.text = builder.toString()
    }

    fun bindSpecialExpandView() {
        ma_settings.apply {
            this.et_tindicators_source.setOnClickListener {
                showPopup(et_tindicators_source, {
                    generateTopHint(hint_ma, this.et_indicators_lenght, this.et_tindicators_source, this.et_indicators_offset)
                }, false)
            }

            this.et_indicators_lenght.setText(chartIndicatorsSettings.maSettings.length.toString())
            this.et_indicators_offset.setText(chartIndicatorsSettings.maSettings.offset.toString())
            this.et_tindicators_source.setText(chartIndicatorsSettings.maSettings.source.toString())

            this.et_indicators_lenght.addTextChangedListener(getTopTextChangeListener(hint_ma, this.et_indicators_lenght, this.et_tindicators_source, this.et_indicators_offset))
            this.et_indicators_offset.addTextChangedListener(getTopTextChangeListener(hint_ma, this.et_indicators_lenght, this.et_tindicators_source, this.et_indicators_offset))
            generateTopHint(hint_ma, this.et_indicators_lenght, this.et_tindicators_source, this.et_indicators_offset)
        }

        ema_settings.apply {
            this.et_tindicators_source.setOnClickListener {
                showPopup(et_tindicators_source, {
                    generateTopHint(hint_ema, this.et_indicators_lenght, this.et_tindicators_source, this.et_indicators_offset)
                }, false)
            }

            this.et_indicators_lenght.setText(chartIndicatorsSettings.emaSettings.length.toString())
            this.et_indicators_offset.setText(chartIndicatorsSettings.emaSettings.offset.toString())
            this.et_tindicators_source.setText(chartIndicatorsSettings.emaSettings.source.toString())

            this.et_indicators_lenght.addTextChangedListener(getTopTextChangeListener(hint_ema, this.et_indicators_lenght, this.et_tindicators_source, this.et_indicators_offset))
            this.et_indicators_offset.addTextChangedListener(getTopTextChangeListener(hint_ema, this.et_indicators_lenght, this.et_tindicators_source, this.et_indicators_offset))
            generateTopHint(hint_ema, this.et_indicators_lenght, this.et_tindicators_source, this.et_indicators_offset)
        }

        bb_settings.apply {
            this.tv_source.gone()
            this.et_tindicators_source.gone()

            this.et_indicators_lenght.setText(chartIndicatorsSettings.bbSettings.length.toString())
            this.et_indicators_offset.setText(chartIndicatorsSettings.bbSettings.mult.toString())

            this.et_indicators_lenght.addTextChangedListener(getTopTextChangeListener(hint_bb, this.et_indicators_lenght, null, this.et_indicators_offset))
            this.et_indicators_offset.addTextChangedListener(getTopTextChangeListener(hint_bb, this.et_indicators_lenght, null, this.et_indicators_offset))
            generateTopHint(hint_bb, this.et_indicators_lenght, null, this.et_indicators_offset)
        }

        vol_settings.apply {
            this.tv_length.gone()
            this.et_indicators_lenght.gone()
            this.tv_source.gone()
            this.et_tindicators_source.gone()

            this.cb_ma.visible()
            this.tv_ma.visible()

            this.et_indicators_offset.gone()
            this.tv_offset.gone()
        }

        macd_settings.apply {
            this.et_bindicators_source.setOnClickListener {
                showPopup(et_bindicators_source, {
                    generateBottomHint(macd_hint, this.et_indicators_fast_lenght, this.et_indicators_show_lenght, this.et_bindicators_source, this.et_indicators_signal)
                }, true)
            }

            this.et_indicators_fast_lenght.setText(chartIndicatorsSettings.macdSettings.fastLength.toString())
            this.et_indicators_show_lenght.setText(chartIndicatorsSettings.macdSettings.showLength.toString())
            this.et_indicators_signal.setText(chartIndicatorsSettings.macdSettings.signalLength.toString())
            this.et_bindicators_source.setText(chartIndicatorsSettings.macdSettings.source.toString())

            this.et_indicators_fast_lenght.addTextChangedListener(getBottomTextChangeListener(macd_hint, this.et_indicators_fast_lenght, this.et_indicators_show_lenght, this.et_bindicators_source, this.et_indicators_signal))
            this.et_indicators_show_lenght.addTextChangedListener(getBottomTextChangeListener(macd_hint, this.et_indicators_fast_lenght, this.et_indicators_show_lenght, this.et_bindicators_source, this.et_indicators_signal))
            this.et_indicators_signal.addTextChangedListener(getBottomTextChangeListener(macd_hint, this.et_indicators_fast_lenght, this.et_indicators_show_lenght, this.et_bindicators_source, this.et_indicators_signal))
            generateBottomHint(macd_hint, this.et_indicators_fast_lenght, this.et_indicators_show_lenght, this.et_bindicators_source, this.et_indicators_signal)
        }

        rsi_settings.apply {
            this.tv_show_length.gone()
            this.et_indicators_show_lenght.gone()
            this.tv_source.gone()
            this.et_bindicators_source.gone()
            this.tv_signal.gone()
            this.et_indicators_signal.gone()

            this.et_indicators_fast_lenght.setText(chartIndicatorsSettings.rsiSettings.length.toString())
            this.et_indicators_fast_lenght.addTextChangedListener(getBottomTextChangeListener(rsi_hint, this.et_indicators_fast_lenght, null, null, null))
            generateBottomHint(rsi_hint, this.et_indicators_fast_lenght, null, null, null)
        }
    }

    fun showPopup(textView: TextView, onChooseSource: () -> Unit, isOpenToTop: Boolean) {
        hideKeyboardFrom()

        var popupView = LayoutInflater.from(context).inflate(R.layout.popup_indicator, null)
        var dialog = PopupWindow(popupView, 125, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        when (textView.text.toString()) {
            "Close" -> popupView.closeTick.visible()
            "Open" -> popupView.openTick.visible()
            "High" -> popupView.highTick.visible()
            "Low" -> popupView.lowTick.visible()
            "hl2" -> popupView.hl2Tick.visible()
            "hlc3" -> popupView.hlc3Tick.visible()
            "ohlc4" -> popupView.ohlc4Tick.visible()
            else -> popupView.closeTick.visible()
        }

        var popupClickListener = View.OnClickListener { textViewPopup ->
            textView.text = (textViewPopup as TextView).text
            onChooseSource()
            dialog.dismiss()
        }

        with(popupView) {
            close.setOnClickListener(popupClickListener)
            open.setOnClickListener(popupClickListener)
            high.setOnClickListener(popupClickListener)
            low.setOnClickListener(popupClickListener)
            hl2.setOnClickListener(popupClickListener)
            hlc3.setOnClickListener(popupClickListener)
            ohlc4.setOnClickListener(popupClickListener)
        }

        GlobalScope.launch(Dispatchers.Main) {
            delay(250)
            if (isOpenToTop)
                dialog.showAsDropDown(textView, 0, -textView.height - 320, Gravity.TOP)
            else
                dialog.showAsDropDown(textView, 0, -1 * 189)
        }
    }

    fun bindButton() {
        btn_close_indicator_selection.setOnClickListener {
            hideKeyboardFrom()
            dismiss()
        }
    }



    private fun getMASettings(): MASettings {
        return MASettings(ma_settings.et_indicators_lenght.text.toString().toIntOrNull() ?: DefaultIndicatorParams.MA_LENGTH,
                SourceParameter.valueOf(checkEmptyString(ma_settings.et_tindicators_source.text.toString())),
                ma_settings.et_indicators_offset.text.toString().toIntOrNull() ?: DefaultIndicatorParams.MA_OFFSET)
    }

    private fun getEMASettings(): EMASettings {
        return EMASettings(ema_settings.et_indicators_lenght.text.toString().toIntOrNull() ?: DefaultIndicatorParams.EMA_LENGTH,
                SourceParameter.valueOf(checkEmptyString(ema_settings.et_tindicators_source.text.toString())),
                ema_settings.et_indicators_offset.text.toString().toIntOrNull() ?: DefaultIndicatorParams.EMA_OFFSET)
    }

    private fun getBBSettings(): BBSettings {
        return BBSettings(bb_settings.et_indicators_lenght.text.toString().toIntOrNull() ?: DefaultIndicatorParams.BB_LENGTH,
                bb_settings.et_indicators_offset.text.toString().toIntOrNull() ?: DefaultIndicatorParams.BB_MULT)
    }

    private fun getVolSettings(): VolSettings {
        return VolSettings(vol_settings.cb_ma.isChecked,
                vol_settings.et_indicators_offset.text.toString().toIntOrNull() ?: DefaultIndicatorParams.VOL_OFFSET)
    }

    private fun getMACDSettings(): MACDSettings {
        return MACDSettings(macd_settings.et_indicators_fast_lenght.text.toString().toIntOrNull() ?: DefaultIndicatorParams.MACD_FAST_LENGTH,
                macd_settings.et_indicators_show_lenght.text.toString().toIntOrNull() ?: DefaultIndicatorParams.MACD_SHOW_LENGTH,
                SourceParameter.valueOf(checkEmptyString(macd_settings.et_bindicators_source.text.toString())),
                macd_settings.et_indicators_signal.text.toString().toIntOrNull() ?: DefaultIndicatorParams.MACD_SIGNAL_LENGTH)
    }

    private fun getRSISetting(): RSISettings {
        return RSISettings(rsi_settings.et_indicators_fast_lenght.text.toString().toIntOrNull() ?: DefaultIndicatorParams.RSI_LENGTH)
    }

    private fun checkEmptyString(str: String): String {
        return if (str == "")
            DefaultIndicatorParams.SOURCE.toString()
        else
            str
    }

    class ExpandedItem(var isExpanded: Boolean = false, var indicator: Indicators? = null, var imageButton: ImageButton? = null)
}

