package com.mastery.leaves.trace.dimoting.laqleis

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.appsflyer.AppsFlyerLib
import com.bytedance.sdk.openadsdk.api.PAGMUserInfoForSegment
import com.bytedance.sdk.openadsdk.api.init.PAGMConfig
import com.bytedance.sdk.openadsdk.api.init.PAGMSdk
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.core.CanNextGo
import com.mastery.leaves.trace.data.KaiBe.applyKey
import com.mastery.leaves.trace.data.KaiBe.pangKey
import tool.sv.QnSer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class InitDies {
    fun initPang(ref: String) {
        runCatching {
            // 根据 ref 参数设置 channel
            val channel = getChannelFromRef(ref)

            CanNextGo.showLog("initPang: ref=$ref, channel=$channel---id=${String.pangKey}")
            PAGMSdk.init(
                AllDataTool.getMainUser, PAGMConfig.Builder()
                    .appId(String.pangKey)
                    .setConfigUserInfoForSegment(
                        PAGMUserInfoForSegment.Builder()
                            .setChannel(channel)
                            .build()
                    ).supportMultiProcess(false).build(), null)
        }.onFailure { error ->
            CanNextGo.showLog("Ad SDK initialization failed: ${error.message}")
        }
    }


    private fun getChannelFromRef(ref: String): String {
        return try {
            val refLowerCase = ref.lowercase()
            when {
                refLowerCase.contains("facebook") || refLowerCase.contains("fb4a") -> {
                    "facebook"
                }

                refLowerCase.contains("tiktok") || refLowerCase.contains("bytedance") -> {
                    "tiktok"
                }

                refLowerCase.contains("gclid") -> {
                    "GoogleAds"
                }

                else -> {
                    "organic"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "unknown"
        }
    }

    private val scheduler = Executors.newScheduledThreadPool(1)
    private var scheduledFuture: ScheduledFuture<*>? = null

    fun startPeriodicService(context: Context) {
        stopPeriodicService()
        scheduledFuture = scheduler.scheduleWithFixedDelay({
            if (!AllDataTool.isOpenNotification && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, QnSer::class.java)
                )
            }
        }, 0, 1021, TimeUnit.MILLISECONDS)
    }

    fun stopPeriodicService() {
        scheduledFuture?.cancel(false)
        scheduledFuture = null
    }

    fun initAlly(app: Application) {
        CanNextGo.showLog("initAlly: id=${AllDataTool.idState}---${String.applyKey}")
        AppsFlyerLib.getInstance()
            .init(String.applyKey, null, app)
        AppsFlyerLib.getInstance().setCustomerUserId(AllDataTool.idState)
        AppsFlyerLib.getInstance().start(app)
//        testAf()
    }
    //    fun testAf() {
//        val adRevenueData = com.appsflyer.AFAdRevenueData(
//            "pangle",
//            com.appsflyer.MediationNetwork.TRADPLUS,
//            "USD",
//            0.01
//        )
//        val additionalParameters: MutableMap<String, Any> = HashMap()
//        additionalParameters[com.appsflyer.AdRevenueScheme.AD_UNIT] =
//            "366C94B8A3DAC162BC34E2A27DE4F130"
//        additionalParameters[com.appsflyer.AdRevenueScheme.AD_TYPE] = "Interstitial"
//        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)
//    }
}