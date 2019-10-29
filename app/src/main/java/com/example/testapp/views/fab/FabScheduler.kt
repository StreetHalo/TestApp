package trade.paper.app.views.fab

import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class FabScheduler(fab: FloatingActionButton) {
    val fab: IFab = SubscriptionFab(fab)

    val executor = ScheduledThreadPoolExecutor(1)
    init {
        startAnimationScheduler()
    }
    fun startAnimationScheduler(){
        executor.scheduleWithFixedDelay({
            fab.shake()
        },0L,20L, TimeUnit.SECONDS)
    }
}