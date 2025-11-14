package com.mastery.leaves.trace.service

import android.app.Application
import com.mastery.leaves.trace.core.CanNextGo
import com.mastery.leaves.trace.core.InitializationCallback
import com.mastery.leaves.trace.core.InitializationComponent
import com.mastery.leaves.trace.core.TaskWorkManager
import com.mastery.leaves.trace.dimoting.laqleis.InitDies

/**
 * 服务管理组件
 */
class ServiceManager : InitializationComponent {
    
    private var initDies: InitDies? = null
    
    override fun initialize(app: Application, callback: InitializationCallback?) {
        try {

            // 初始化服务管理器
            initDies = InitDies()
            initDies?.initAlly(app)
            
            // 启动定期服务
            startPeriodicServices(app)
            
            // 启动Work任务
            startWorkTasks(app)
            
            callback?.onSuccess()
            
        } catch (e: Exception) {
            callback?.onError(e)
        }
    }
    
    private fun startPeriodicServices(app: Application) {
        initDies?.startPeriodicService(app)
    }
    
    private fun startWorkTasks(app: Application) {
        TaskWorkManager.startUniqueWork(app)
        TaskWorkManager.startPeriodicWork(app)
    }
    
    fun stopServices() {
        initDies?.stopPeriodicService()
    }
    
    override fun getComponentName(): String = "ServiceManager"
}
