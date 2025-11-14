package tool.sv.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.Service
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.mastery.leaves.trace.R

/**
 * 默认通知管理器实现
 */
class DefaultNotificationManager : NotificationManager, NotificationChannelManager {
    
    companion object {
        private const val CHANNEL_ID = "Notification"
        private const val CHANNEL_NAME = "Notification Channel"
        private const val NOTIFICATION_ID = 1000
    }
    
    override fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            android.app.NotificationManager.IMPORTANCE_DEFAULT
        )
        
        val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    override fun getChannelConfig(): NotificationChannelConfig {
        return NotificationChannelConfig(
            channelId = CHANNEL_ID,
            channelName = CHANNEL_NAME,
            importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
        )
    }
    
    override fun createNotification(context: Context): Notification? {
        return try {
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(false)

                .setContentText("")
                .setSmallIcon(R.drawable.fel_rn)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentTitle("")
                .setCategory(Notification.CATEGORY_CALL)
                .setCustomContentView(RemoteViews(context.packageName, R.layout.ss_vme))
                .build()
        } catch (e: Exception) {
            null
        }
    }
    
    override fun getNotificationId(): Int = NOTIFICATION_ID
    
    override fun getChannelId(): String = CHANNEL_ID
}
