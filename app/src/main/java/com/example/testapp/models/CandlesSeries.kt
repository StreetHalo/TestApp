package trade.paper.app.models

import java.util.*

class CandlesSeries(){
    var date: MutableList<Date> = mutableListOf()
    var open: MutableList<Double> = mutableListOf()
    var close: MutableList<Double> = mutableListOf()
    var high: MutableList<Double> = mutableListOf()
    var low: MutableList<Double> = mutableListOf()
}