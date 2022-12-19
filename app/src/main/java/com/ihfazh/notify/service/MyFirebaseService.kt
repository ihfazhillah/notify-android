package com.ihfazh.notify.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ihfazh.notify.MainActivity
import com.ihfazh.notify.R
import com.ihfazh.notify.auth.AuthRepository
import com.ihfazh.notify.common.PreferenceManager
import com.ihfazh.notify.destinations.ItemDetailDestination
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import timber.log.Timber


class MyFirebaseService(): FirebaseMessagingService() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val authRepository : AuthRepository by inject()
    private val preferenceManager: PreferenceManager by inject()


    override fun onNewToken(token: String) {
        Timber.d("token : $token")
        scope.launch {
            FirebaseInstallations.getInstance().id.addOnSuccessListener { id ->
                scope.launch {
                    if (authRepository.getToken() != null) authRepository.registerDevice(id, token)
                    else preferenceManager.setRegistrationId(token)
                }
            }
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("data: ${remoteMessage.data}")
        Timber.d("notification: ${remoteMessage.notification}")

        val id = remoteMessage.data["id"]
        val title = remoteMessage.data["title"]
        val guid = remoteMessage.data["guid"]
        showNotification(title, "Job Alert: $title}", id?.toInt())

//        if (remoteMessage.notification != null) {
//            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
//        }
    }

    @SuppressLint("ServiceCast")
    private fun showNotification(title: String?, body: String?, id: Int? = null) {
        val channelId = "job_alert"

//        val uri = ItemDetailDestionas
        val uri = "https://notify.ihfazh.com/deeplink/${ItemDetailDestination(id=id ?: 10).route}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri, this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }


//        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
//            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val channelName = "Job Alert"
        val channelDescription = "New Job alert from upwork feeds"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(id ?: 0, notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}