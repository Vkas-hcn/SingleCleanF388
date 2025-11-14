package tool.sv.factory

import tool.sv.config.DefaultServiceConfig
import tool.sv.config.ServiceConfig
import tool.sv.lifecycle.DefaultServiceLifecycleManager
import tool.sv.lifecycle.DefaultServiceStateManager
import tool.sv.lifecycle.ServiceLifecycleManager
import tool.sv.lifecycle.ServiceStateManager
import tool.sv.notification.DefaultNotificationManager
import tool.sv.notification.NotificationChannelManager
import tool.sv.notification.NotificationManager

/**
 * 服务组件工厂
 * 负责创建和管理各个服务组件的实例
 */
object ServiceComponentFactory {
    
    /**
     * 创建通知管理器
     */
    fun createNotificationManager(): NotificationManager {
        return DefaultNotificationManager()
    }
    
    /**
     * 创建通知渠道管理器
     */
    fun createNotificationChannelManager(): NotificationChannelManager {
        return DefaultNotificationManager()
    }
    
    /**
     * 创建状态管理器
     */
    fun createStateManager(): ServiceStateManager {
        return DefaultServiceStateManager()
    }
    
    /**
     * 创建服务配置
     */
    fun createServiceConfig(): ServiceConfig {
        return DefaultServiceConfig()
    }
    
    /**
     * 创建生命周期管理器
     */
    fun createLifecycleManager(
        notificationManager: NotificationManager = createNotificationManager(),
        channelManager: NotificationChannelManager = createNotificationChannelManager(),
        stateManager: ServiceStateManager = createStateManager()
    ): ServiceLifecycleManager {
        return DefaultServiceLifecycleManager(
            notificationManager = notificationManager,
            channelManager = channelManager,
            stateManager = stateManager
        )
    }
    
    /**
     * 创建完整的服务组件集合
     */
    fun createServiceComponents(): ServiceComponents {
        val notificationManager = createNotificationManager()
        val stateManager = createStateManager()
        val config = createServiceConfig()
        val lifecycleManager = createLifecycleManager(
            notificationManager = notificationManager,
            channelManager = notificationManager as NotificationChannelManager,
            stateManager = stateManager
        )
        
        return ServiceComponents(
            notificationManager = notificationManager,
            stateManager = stateManager,
            serviceConfig = config,
            lifecycleManager = lifecycleManager
        )
    }
}

/**
 * 服务组件集合
 */
data class ServiceComponents(
    val notificationManager: NotificationManager,
    val stateManager: ServiceStateManager,
    val serviceConfig: ServiceConfig,
    val lifecycleManager: ServiceLifecycleManager
)
