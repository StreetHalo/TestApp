package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import kotlinx.android.synthetic.main.item_markets.view.*
import trade.paper.app.listeners.DataCacheListener

class HomeFavoriteRecyclerAdapter(onClickListener: View.OnClickListener?, reload: () -> Unit?) : MarketsRecyclerAdapter(onClickListener, reload),
    DataCacheListener {

    private val TYPE_ITEM = R.layout.item_markets
    private val TYPE_ADD_TO_FAVORITE = R.layout.item_add_favorite_pair

    var goToMarkets: (() -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return if (position == data.size) TYPE_ADD_TO_FAVORITE else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
                LayoutInflater
                        .from(parent.context)
                        .inflate(
                                viewType,
                                parent,
                                false
                        ),
                onClickListener
        )

        if (viewType == TYPE_ADD_TO_FAVORITE)
            viewHolder.itemView.setOnClickListener {
                val pos = viewHolder.adapterPosition
                if (pos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    goToMarkets?.invoke()
                }
            }
        if (viewType == TYPE_ITEM)
            viewHolder.itemView.btn_add_to_favorite.setOnClickListener {
                val pos = viewHolder.adapterPosition
                if (pos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    onFavoriteBtnClick?.invoke(pos)
                }
            }

        return viewHolder
    }

    override fun getItemCount(): Int = data.size + 1

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (position != data.size)
            super.onBindViewHolder(holder, position)
    }
}