package tool.sv.config

/**
 * 服务配置接口
 */
interface ServiceConfig {
    /**
     * 获取通知ID
     */
    fun getNotificationId(): Int
    
    /**
     * 获取服务启动模式
     */
    fun getStartMode(): Int
    
    /**
     * 是否启用前台服务
     */
    fun isForegroundServiceEnabled(): Boolean
}

/**
 * 默认服务配置
 */
class DefaultServiceConfig : ServiceConfig {
    
    companion object {
        private const val DEFAULT_NOTIFICATION_ID = 1000
        private const val DEFAULT_START_MODE = android.app.Service.START_STICKY
    }
    
    override fun getNotificationId(): Int = DEFAULT_NOTIFICATION_ID
    
    override fun getStartMode(): Int = DEFAULT_START_MODE
    
    override fun isForegroundServiceEnabled(): Boolean = true
}
