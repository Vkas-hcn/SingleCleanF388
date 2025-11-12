package com.mastery.leaves.trace.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.mastery.leaves.trace.ami.AllDataTool
import java.util.UUID
import kotlin.runCatching
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.isNullOrEmpty

object RefGoTool {
    private var referrerClient: InstallReferrerClient? = null
    private val handler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null
    private var isGettingReferrer = false


    fun fetchInstallReferrer(context: Context) {
        // 如果已经有 referrer，不再获取
        if (AllDataTool.refState.isNotEmpty()) {
            RefAndUserData.rAndData()
            return
        }

        // 如果正在获取中，不重复执行
        if (isGettingReferrer) {
            return
        }

        isGettingReferrer = true

        try {
            // 清理之前的 client
            referrerClient?.endConnection()
            
            referrerClient = InstallReferrerClient.newBuilder(context).build()
            
            // 设置60秒超时
            timeoutRunnable = Runnable {
                cleanup()
                // 延迟5秒后重试
                handler.postDelayed({
                    fetchInstallReferrer(context)
                }, 5000)
            }
            handler.postDelayed(timeoutRunnable!!, 60000)
            
            referrerClient?.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            try {
                                val response = referrerClient?.installReferrer
                                if (response != null) {
                                    val referrer = response.installReferrer
                                    AllDataTool.rone = response.referrerClickTimestampSeconds.toString()
                                    AllDataTool.rtow = response.referrerClickTimestampServerSeconds.toString()
                                    AllDataTool.refState = referrer
                                    RefAndUserData.rAndData()
                                    // 成功获取，取消超时并清理
                                    handler.removeCallbacks(timeoutRunnable!!)
                                    cleanup()
                                } else {
                                    Log.e("TAG", "fetchInstallReferrer: response为null")
                                    onFailed("response为null")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                onFailed("处理referrer异常: ${e.message}")
                            }
                        }
                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            onFailed("Function not supported")
                        }
                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            onFailed("Service unavailable")
                        }
                        else -> {
                            onFailed("Unknown response code: $responseCode")
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    onFailed("")
                }
                
                private fun onFailed(reason: String) {
                    // 取消超时
                    handler.removeCallbacks(timeoutRunnable!!)
                    cleanup()
                    
                    // 延迟5秒后重试
                    handler.postDelayed({
                        fetchInstallReferrer(context)
                    }, 5000)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            cleanup()
            
            // 延迟5秒后重试
            handler.postDelayed({
                fetchInstallReferrer(context)
            }, 5000)
        }
    }

    /**
     * 清理资源
     */
    private fun cleanup() {
        isGettingReferrer = false
        try {
            referrerClient?.endConnection()
        } catch (e: Exception) {
            // 忽略清理时的异常
        }
        referrerClient = null
    }

}