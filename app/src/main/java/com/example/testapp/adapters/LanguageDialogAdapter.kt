package trade.paper.app.adapters

import android.content.Context
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.example.testapp.R
import trade.paper.app.models.hawk.Settings
import java.util.*

class LanguageDialogAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private val data = ArrayList<Pair<String, String>>()
    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        val view = createView(p0.context)

        val viewHolder = object : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {}

        view.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION && !it.isSelected) {
                onItemClick?.invoke(data[position].first)
            }
        }

        return viewHolder
    }

    fun setData(data: List<Pair<String, String>>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
        (p0.itemView as TextView).text = data[p1].second
        p0.itemView.isSelected = "en" == data[p1].first
    }

    private fun createView(context: Context): TextView {
        val height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                36f,
                context.resources.displayMetrics
        ).toInt()

        val verticalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                context.resources.displayMetrics
        ).toInt()
        val horizontalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                context.resources.displayMetrics
        ).toInt()

        return TextView(context).apply {
            layoutParams = androidx.recyclerview.widget.RecyclerView.LayoutParams(androidx.recyclerview.widget.RecyclerView.LayoutParams.MATCH_PARENT, height).apply {
                setMargins(horizontalMargin, 0, horizontalMargin, verticalMargin)
            }
            setTextColor(resources.getColor(R.color.button_filter_textcolor))
            background = resources.getDrawable(R.drawable.btn_language_settings)
            gravity = Gravity.CENTER

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             //   foreground = DrawableUtils.getSelectedItemDrawable(context)
            }
        }
    }
}