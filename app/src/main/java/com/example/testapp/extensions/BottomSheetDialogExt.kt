package trade.paper.app.utils.extensions

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.View
import android.widget.FrameLayout

fun BottomSheetDialog.disableScroll(){
    val bottomSheet = this.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
    BottomSheetBehavior.from(bottomSheet).apply {
        state = BottomSheetBehavior.STATE_EXPANDED
        setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                    this@apply.setState(BottomSheetBehavior.STATE_EXPANDED)
                }
            }
        })
    }
}