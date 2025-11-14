package tool.sv.notification

import android.app.Notification
import android.content.Context

/**
 * 通知管理接口
 */
interface NotificationManager {
    /**
     * 创建通知
     */
    fun createNotification(context: Context): Notification?
    
    /**
     * 获取通知ID
     */
    fun getNotificationId(): Int
    
    /**
     * 获取通知渠道ID
     */
    fun getChannelId(): String
}

/**
 * 通知渠道管理接口
 */
interface NotificationChannelManager {
    /**
     * 创建通知渠道
     */
    fun createNotificationChannel(context: Context)
    
    /**
     * 获取渠道配置
     */
    fun getChannelConfig(): NotificationChannelConfig
}

/**
 * 通知渠道配置
 */
data class NotificationChannelConfig(
    val channelId: String,
    val channelName: String,
    val importance: Int
)
