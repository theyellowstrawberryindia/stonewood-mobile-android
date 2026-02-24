package uk.co.savills.stonewood.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.screen.MainActivity

class NotificationManager(
    private val appContext: Context
) {
    private val notificationManager by lazy {
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun sendNotification(title: String, message: String, bigText: String? = null) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val launchActivityIntent = Intent(appContext, MainActivity::class.java)
        launchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val notification = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setOngoing(false)
            .setWhen(0)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(PendingIntent.getActivity(appContext, 0, launchActivityIntent, 0))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setContentText(message)
            .setContentTitle(title)
            .setLargeIcon(
                BitmapFactory.decodeResource(appContext.resources, R.drawable.ic_logo_small)
            )
            .setColor(ContextCompat.getColor(appContext, R.color.colorPrimary))
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText ?: message))
            .build()

        notificationManager.notify(title, 0, notification)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "Notifications"
        private const val NOTIFICATION_CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.notifications"
    }
}
