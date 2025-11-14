package tool.sv.lifecycle

import android.app.Notification
import android.app.Service
import android.content.Intent
import com.mastery.leaves.trace.ami.AllDataTool
import tool.sv.notification.NotificationChannelManager
import tool.sv.notification.NotificationManager

/**
 * 默认服务生命周期管理器
 */
class DefaultServiceLifecycleManager(
    private val notificationManager: NotificationManager,
    private val channelManager: NotificationChannelManager,
    private val stateManager: ServiceStateManager
) : ServiceLifecycleManager, ForegroundServiceManager {
    
    private var cachedNotification: Notification? = null
    
    override fun onCreate(service: Service) {
        channelManager.createNotificationChannel(service)
        cachedNotification = notificationManager.createNotification(service)
        stateManager.setNotificationState(true)
    }
    
    override fun onStartCommand(service: Service, intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService(service)
        return getStartMode()
    }
    
    override fun onDestroy(service: Service) {
        stateManager.setNotificationState(false)
        stopForegroundService(service)
    }
    
    override fun startForegroundService(service: Service): Boolean {
        return try {
            cachedNotification?.let { notification ->
                service.startForeground(notificationManager.getNotificationId(), notification)
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    override fun stopForegroundService(service: Service) {
        try {
            service.stopForeground(true)
        } catch (e: Exception) {
            // 忽略停止前台服务时的异常
        }
    }
    
    override fun getStartMode(): Int = Service.START_STICKY  // 必须用这个模式
}

/**
 * 默认状态管理器
 */
class DefaultServiceStateManager : ServiceStateManager {
    
    override fun setNotificationState(isOpen: Boolean) {
        AllDataTool.isOpenNotification = isOpen
    }
    
    override fun getNotificationState(): Boolean {
        return AllDataTool.isOpenNotification
    }
}
