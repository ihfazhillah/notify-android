package com.ihfazh.notify.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ihfazh.notify.feed.FeedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MarkAsBroadcastReceiverHandler(
): KoinComponent{
    private val repository: FeedRepository by inject()

    fun run(coroutineScope: CoroutineScope, id: Int){
        coroutineScope.launch {
            repository.log(id)
        }
    }

}

class MarkAsReadBroadcastReceiver: BroadcastReceiver(){
    private val scope = CoroutineScope(SupervisorJob())

    private val handler = MarkAsBroadcastReceiverHandler()


    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        try {
            intent?.getIntExtra(ITEM_ID_KEY, 0)?.let { itemId ->
                if (itemId != 0){
                    Timber.d("Id: $itemId")
                    handler.run(scope, itemId)
                    (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(itemId)
                }
            }
        } finally {
            pendingResult.finish()
        }

    }

    companion object {
        const val ITEM_ID_KEY = "item_id_key"
    }
}