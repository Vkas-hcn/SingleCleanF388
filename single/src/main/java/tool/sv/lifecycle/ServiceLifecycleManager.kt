package tool.sv.lifecycle

import android.app.Service
import android.content.Intent

/**
 * 服务生命周期管理接口
 */
interface ServiceLifecycleManager {
    /**
     * 服务创建时调用
     */
    fun onCreate(service: Service)
    
    /**
     * 服务启动命令时调用
     */
    fun onStartCommand(service: Service, intent: Intent?, flags: Int, startId: Int): Int
    
    /**
     * 服务销毁时调用
     */
    fun onDestroy(service: Service)
}

/**
 * 前台服务管理接口
 */
interface ForegroundServiceManager {
    /**
     * 启动前台服务
     */
    fun startForegroundService(service: Service): Boolean
    
    /**
     * 停止前台服务
     */
    fun stopForegroundService(service: Service)
    
    /**
     * 获取服务启动模式
     */
    fun getStartMode(): Int
}

/**
 * 状态管理接口
 */
interface ServiceStateManager {
    /**
     * 设置通知状态
     */
    fun setNotificationState(isOpen: Boolean)
    
    /**
     * 获取通知状态
     */
    fun getNotificationState(): Boolean
}
