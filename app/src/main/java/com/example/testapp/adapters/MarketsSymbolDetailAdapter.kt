package trade.paper.app.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.content.res.Configuration
import trade.paper.app.models.hawk.Settings
import java.util.*

class MarketsSymbolDetailAdapter(fm: FragmentManager, private val symbol: String): FragmentPagerAdapter(fm) {
    override fun getItem(pos: Int): Fragment {
        val fragment = Fragment()
        (fragment as Fragment).arguments = Bundle().apply {
            putString(trade.paper.app.models.TAG.SYMBOL.tag, symbol)
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
       // val context = App.instance
      //  val configuration = Configuration(context.resources.configuration)
     //   configuration.setLocale(Locale(Settings.getLocale()))
      //  val resources = context.createConfigurationContext(configuration).getResources()
        return "   "
    }

    override fun getCount() = 2
}