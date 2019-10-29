package trade.paper.app.models.cache

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import trade.paper.app.models.WithdrawCurrencyAccuracy
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object AppCache {

    private val CURRENCY_FULLNAME = 0
    private val CURRENCY_NAME = 1
    private val CURRENCY_ACCURACY = 2
    private val bgHandler: Handler

    private val READ_DEBUG = "WITHDRAW_ACCURACY_DEBUG"

  //  var orderbookSelectorType = DetailOrderbook.OrderbookTypeEnum.AMOUNT
  //  var orderbookExchangeSelectorType = OrderbookExchangeSelectorEnum.AMOUNT

    var accountSearch = ""

    var withdrawCurrencyAccuracies = HashMap<String, Int>()

    init {
        val handlerThread = HandlerThread("background")
        handlerThread.start()
        bgHandler = Handler(handlerThread.looper)
        readWithdrawCurrencyAccuraciesFromFile()
    }

    private fun readWithdrawCurrencyAccuraciesFromFile() {
        var fileReader: BufferedReader? = null

        try {


        } catch (e: Exception) {
            Log.d(READ_DEBUG, e.message)
        } finally {
            try {
                fileReader!!.close()
            } catch (e: IOException) {
                Log.d(READ_DEBUG, e.message)
            }
        }
    }
}