package trade.paper.app.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.testapp.BaseFragment

open class TradesTabAdapter(var fm: FragmentManager) : FragmentStatePagerAdapter(fm) {





        //open var fragments: MutableList<BaseFragment> = mutableListOf()
        private var titles: MutableList<String> = mutableListOf()

        fun clear(){
       //     fragments.clear()
        }

        override fun getItem(position: Int): Fragment {
         return Fragment()
        }

        override fun getCount(): Int {
            return 2
        }

        fun addFragment(fragment: BaseFragment, title: String) {
          //  fragments.add(fragment)
          //  titles.add(Formatter.currency(title))
        }

        fun addFragment(fragment: BaseFragment, title: String, position: Int) {
           // fragments.add(position, fragment)
           // titles.add(position, Formatter.currency(title))
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }

   /*     fun getFragment(position: Int): MarketsTabBaseFragment? {
            return (fragments.getOrNull(position) as MarketsTabBaseFragment?)
        }*/

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        fun getTabPosition(symbol: String): Int{
            var position = 0
            for (i in 0 until titles.size){
                if (titles[i] == symbol){
                    position = i
                    break
                }
            }
            return position
        }

        fun removeFavoriteFragment(): Boolean {
            return try {
              //  fragments.removeAt(0)
                titles.removeAt(0)
                true
            }catch (e: Exception){
              //  ErrorWrapper.onError(e)
                false
            }
        }


    }
