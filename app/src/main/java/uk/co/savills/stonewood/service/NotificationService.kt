package uk.co.savills.stonewood.service

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.microsoft.windowsazure.messaging.NotificationHub
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.MainApplication

class NotificationService : FirebaseMessagingService() {
    private val appContainer
        get() = (application as MainApplication).appContainer

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val hub = NotificationHub(
            NOTIFICATION_HUB_NAME,
            NOTIFICATION_HUB_CONNECTION_SIGNATURE,
            this
        )

        if (appContainer.authService.isLoggedIn) {
            val userId = requireNotNull(appContainer.appState.profile).id.toString()
            val tag = "${userId}_$buildVariantTag"
            hub.register(token, tag)
        } else {
            hub.unregister()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        appContainer.notificationManager.sendNotification(
            remoteMessage.title,
            remoteMessage.message,
            remoteMessage.bigText
        )
    }

    private val RemoteMessage.title
        get() = requireNotNull(data["title"])

    private val RemoteMessage.message
        get() = requireNotNull(data["message"])

    private val RemoteMessage.bigText
        get() = data["bigText"]

    companion object {
        private const val NOTIFICATION_HUB_NAME = "stonewood"
        private const val NOTIFICATION_HUB_CONNECTION_SIGNATURE =
            "Endpoint=sb://stonewood.servicebus.windows.net/;" +
                "SharedAccessKeyName=DefaultListenSharedAccessSignature;" +
                "SharedAccessKey=R0Y3z9/VbzEil31bku4amCXIp4e74g91uPOlJ7g0vzg="

        fun register(context: Context, userId: String) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                val hub = NotificationHub(
                    NOTIFICATION_HUB_NAME,
                    NOTIFICATION_HUB_CONNECTION_SIGNATURE,
                    context
                )
                val tag = "${userId}_$buildVariantTag"
                GlobalScope.launch(Dispatchers.IO) {
                    hub.register(token, tag)
                }
            }
        }

        fun unregister(context: Context) {
            val hub = NotificationHub(
                NOTIFICATION_HUB_NAME,
                NOTIFICATION_HUB_CONNECTION_SIGNATURE,
                context
            )
            hub.unregister()
        }
    }
}

private val buildVariantTag
    get() = when (BuildConfig.BUILD_TYPE) {
        "release" -> "RELEASE"
        "staging" -> "TEST"
        "development" -> "DEVELOPMENT"
        "debug" -> "DEBUG"
        else -> throw IllegalStateException("${BuildConfig.BUILD_TYPE} build not configured")
    }
