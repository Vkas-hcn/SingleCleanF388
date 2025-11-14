package com.mastery.leaves.trace.core

import android.app.Application
import android.util.Log
import com.mastery.leaves.trace.device.DeviceInitializer
import com.mastery.leaves.trace.network.NetworkInitializer
import com.mastery.leaves.trace.secret.SecretOperationManager
import com.mastery.leaves.trace.service.ServiceManager

/**
 * 应用初始化入口点 - 重构为协调器模式
 * 负责协调各个初始化组件的执行
 */
object CanNextGo {
    
    private val coordinator = AppInitializationCoordinator()
    private var isInitialized = false
    
    fun showLog(log: String) {
//        Log.e("Single", log)
    }
    

    fun Gined(app: Application) {
        if (isInitialized) {
            return
        }
        

        // 设置全局回调
        coordinator.setGlobalCallback(object : InitializationCallback {
            override fun onSuccess() {
                isInitialized = true
            }
            
            override fun onError(error: Throwable) {
                showLog("CanNextGo: An error occurred during application initialization process - ${error.message}")
            }
        })
        
        // 添加各个初始化组件
        setupInitializationComponents()
        
        // 开始初始化
        coordinator.startInitialization(app)
    }
    
    /**
     * 设置初始化组件
     */
    private fun setupInitializationComponents() {
        // 1. 设备相关初始化（最高优先级，其他组件依赖它）
        coordinator.addComponent(DeviceInitializer())
        // 4. 秘密操作初始化
        coordinator.addComponent(SecretOperationManager())
        // 2. 服务管理初始化
        coordinator.addComponent(ServiceManager())
        
        // 3. 网络相关初始化
        coordinator.addComponent(NetworkInitializer())
        

    }

}