package com.mastery.leaves.trace.core

import android.app.Application
import java.util.concurrent.atomic.AtomicInteger

/**
 * 应用初始化协调器
 * 负责协调各个组件的初始化顺序和依赖关系
 */
class AppInitializationCoordinator : InitializationCoordinator {
    
    private val components = mutableListOf<InitializationComponent>()
    private var globalCallback: InitializationCallback? = null
    private val completedCount = AtomicInteger(0)
    private val errorCount = AtomicInteger(0)
    
    override fun addComponent(component: InitializationComponent) {
        components.add(component)
    }
    
    override fun startInitialization(app: Application) {

        if (components.isEmpty()) {
            globalCallback?.onSuccess()
            return
        }
        
        // 重置计数器
        completedCount.set(0)
        errorCount.set(0)
        
        // 并行初始化所有组件
        components.forEach { component ->
            component.initialize(app, createComponentCallback(component))
        }
    }
    
    override fun setGlobalCallback(callback: InitializationCallback?) {
        this.globalCallback = callback
    }
    
    private fun createComponentCallback(component: InitializationComponent): InitializationCallback {
        return object : InitializationCallback {
            override fun onSuccess() {
                val completed = completedCount.incrementAndGet()
//                CanNextGo.showLog("AppInitializationCoordinator: ${component.getComponentName()} 初始化成功 ($completed/${components.size})")
                
                checkAllCompleted()
            }
            
            override fun onError(error: Throwable) {
                val errors = errorCount.incrementAndGet()
                val completed = completedCount.incrementAndGet()
//                CanNextGo.showLog("AppInitializationCoordinator: ${component.getComponentName()} 初始化失败 - ${error.message}")
                
                checkAllCompleted()
            }
        }
    }
    
    private fun checkAllCompleted() {
        val total = completedCount.get()
        if (total >= components.size) {
            val errors = errorCount.get()
            if (errors > 0) {
//                CanNextGo.showLog("AppInitializationCoordinator: 初始化完成，但有 $errors 个组件失败")
                globalCallback?.onError(RuntimeException("$errors components failed to initialize"))
            } else {
//                CanNextGo.showLog("AppInitializationCoordinator: 所有组件初始化成功")
                globalCallback?.onSuccess()
            }
        }
    }
    
    fun getComponentCount(): Int = components.size
    fun getCompletedCount(): Int = completedCount.get()
    fun getErrorCount(): Int = errorCount.get()
}
