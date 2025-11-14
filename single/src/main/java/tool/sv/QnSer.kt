package tool.sv

import android.app.Service
import android.content.Intent
import android.os.IBinder
import tool.sv.factory.ServiceComponentFactory
import tool.sv.factory.ServiceComponents


class QnSer : Service() {
    
    // 使用工厂创建服务组件，便于管理和测试
    private val components: ServiceComponents by lazy {
        ServiceComponentFactory.createServiceComponents()
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        
        // 委托给生命周期管理器处理
        // 这里会执行原来的所有逻辑：
        // 1. 创建通知渠道
        // 2. 创建通知
        // 3. 设置 AllDataTool.isOpenNotification = true
        components.lifecycleManager.onCreate(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 委托给生命周期管理器处理，保持原有的返回值和行为
        // 这里会执行原来的所有逻辑：
        // 1. 调用 startForeground(1000, mNotification)
        // 2. 返回 START_STICKY
        return components.lifecycleManager.onStartCommand(this, intent, flags, startId)
    }

    override fun onDestroy() {
        // 委托给生命周期管理器处理
        // 这里会执行原来的所有逻辑：
        // 1. 设置 AllDataTool.isOpenNotification = false
        // 2. 停止前台服务
        components.lifecycleManager.onDestroy(this)
        super.onDestroy()
    }
}