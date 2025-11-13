package com.mastery.leaves.trace.ami

import android.os.Handler
import android.os.Looper
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.mastery.leaves.trace.ami.DataPgTool.RequestResult
import com.mastery.leaves.trace.core.CanNextGo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.random.Random

object ChongTool {
    private const val TAG = "GetJkUtils"
    private const val REQUIRED_MAX_RETRY = 20
    private const val OPTIONAL_MIN_RETRY = 2
    private const val OPTIONAL_MAX_RETRY = 5
    private const val MIN_DELAY_MS = 10_000L
    private const val MAX_DELAY_MS = 40_000L

    private val handler = Handler(Looper.getMainLooper())
    private val requestingKeys = mutableSetOf<String>()



    fun getAUTool(jsonObject: JSONObject): Boolean {
        val user = jsonObject.getString("canpa")
        return user == "need"
    }

    fun getShangTool(jsonObject: JSONObject): Boolean {
        try {
            val user = jsonObject.optString("m_d")
            return user == "upgo"
        } catch (e: Exception) {
            return false
        }
    }

    fun initFb(jsonObject: JSONObject) {
        try {
            val fbStr = jsonObject.optString("CvsdvG").split("-")[0]
            val token = jsonObject.optString("CvsdvG").split("-")[1]
            if (fbStr.isBlank()) return
            if (token.isBlank()) return
            if (FacebookSdk.isInitialized()) return
            FacebookSdk.setApplicationId(fbStr)
            FacebookSdk.setClientToken(token)
            FacebookSdk.sdkInitialize(AllDataTool.getMainUser)
            AppEventsLogger.Companion.activateApp(AllDataTool.getMainUser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 上报事件（带重试）
     */
    fun postPointFun(
        canRetry: Boolean,
        name: String,
        key1: String? = null,
        keyValue1: Any? = null
    ) {
        val requestKey = "point_$name"
        val maxRetry = if (canRetry) REQUIRED_MAX_RETRY else Random.nextInt(
            OPTIONAL_MIN_RETRY,
            OPTIONAL_MAX_RETRY + 1
        )
        if (!canRetry && AllDataTool.dataState.isNotBlank() && !getShangTool(JSONObject(AllDataTool.dataState))) {
            return
        }
        executeWithRetry(
            requestKey = requestKey,
            maxRetry = maxRetry,
            taskName = "postPointFun[$name]",
            dataProvider = { DataPing.upPointJson(name, key1, keyValue1) }
        )
    }

    /**
     * 上报广告事件（必传，带重试）
     */
    fun postAdJson(jsonData: String) {
        val requestKey = "ad_${jsonData.hashCode()}"

        executeWithRetry(
            requestKey = requestKey,
            maxRetry = REQUIRED_MAX_RETRY,
            taskName = "postAdJson",
            dataProvider = { DataPing.upAdJson(jsonData) }
        )
    }

    /**
     * 上报安装事件（必传，带重试）
     */
    fun postInstallJson() {
        if (AllDataTool.stateIns) return
        executeWithRetry(
            requestKey = "install",
            maxRetry = REQUIRED_MAX_RETRY,
            taskName = "postInstallJson",
            dataProvider = { DataPing.upInstallJson() },
            onSuccessCallback = { AllDataTool.stateIns = true }
        )
    }

    /**
     * 带重试机制的请求执行器
     */
    private fun executeWithRetry(
        requestKey: String,
        maxRetry: Int,
        taskName: String,
        dataProvider: () -> String,
        onSuccessCallback: (() -> Unit)? = null,
        currentAttempt: Int = 0
    ) {
        try {
            // 防止重复请求
            if (requestingKeys.contains(requestKey)) {
                return
            }

            // 标记为请求中
            requestingKeys.add(requestKey)

            val jsonData = dataProvider()
            CanNextGo.showLog("post-${taskName}-json: ${jsonData}")
            DataPgTool.instance.postPutData(jsonData, { result ->
                when (result) {
                    is RequestResult.Success -> {
                        requestingKeys.remove(requestKey)
                        onSuccessCallback?.invoke()
                        CanNextGo.showLog("post-${taskName}-Success: ${result.data}")
                    }
                    is RequestResult.Error -> {
                        CanNextGo.showLog("post-${taskName}-error: ${result.message}")
                        if (currentAttempt < maxRetry) {
                            // 计算随机延迟时间
                            val delayMs = Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS + 1)

                            // 延迟后重试
                            handler.postDelayed({
                                requestingKeys.remove(requestKey)
                                executeWithRetry(
                                    requestKey,
                                    maxRetry,
                                    taskName,
                                    dataProvider,
                                    onSuccessCallback,
                                    currentAttempt + 1
                                )
                            }, delayMs)
                        } else {
                            requestingKeys.remove(requestKey)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            CanNextGo.showLog("post-${taskName}-error: ${e.message}")

            requestingKeys.remove(requestKey)
        }
    }


    fun ssPostFun() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true){
                postPointFun(false, "session")
                delay(15 * 60 * 1000)
            }
        }
    }

    fun ConfigG(typeUser: Boolean, codeInt: String?) {
        val isuserData: String? = if (codeInt == null) {
            null
        } else if (codeInt != "200") {
            codeInt
        } else if (typeUser) {
            "a"
        } else {
            "b"
        }
        postPointFun(true, "config_G", "getstring", isuserData)
    }
}