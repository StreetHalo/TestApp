package trade.paper.app.adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.example.testapp.R

class ReportsPageAdapter(fr: FragmentManager, var parent: Context?) : FragmentPagerAdapter(fr) {
    override fun getItem(position: Int): Fragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        return 2
    }

    private var currentFragment: Fragment? = null
    private var previousFragment: Fragment? = null
    var onPageChange: ((Fragment) -> Unit)? = null

}