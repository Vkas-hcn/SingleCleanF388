package com.mastery.leaves.trace.ami

import android.content.pm.PackageManager
import android.os.Build
import org.json.JSONObject
import java.util.UUID

/**
 * 设备信息收集器
 * 负责收集设备相关的基础信息
 */
class DeviceInfoCollector {
    
    /**
     * 获取基础设备信息
     */
    fun getBaseDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            packageName = AllDataTool.getMainUser.packageName,
            appVersion = DataPgTool.instance.showAppVersion(),
            distinctId = AllDataTool.idState,
            logId = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            manufacturer = Build.MANUFACTURER,
            deviceBrand = Build.BRAND,
            osVersion = Build.VERSION.RELEASE,
            androidId = AllDataTool.idState
        )
    }
    
    /**
     * 获取安装相关信息
     */
    fun getInstallInfo(): InstallInfo {
        return InstallInfo(
            buildId = "build/${Build.ID}",
            referrerUrl = AllDataTool.refState,
            firstInstallTime = getFirstInstallTime()
        )
    }
    
    /**
     * 获取用户相关信息
     */
    fun getUserInfo(): UserInfo? {
        return if (hasUserData()) {
            UserInfo(userCode = getUserCode())
        } else {
            null
        }
    }
    
    /**
     * 获取首次安装时间
     */
    private fun getFirstInstallTime(): Long {
        return try {
            val packageInfo = AllDataTool.getMainUser.packageManager.getPackageInfo(
                AllDataTool.getMainUser.packageName, 0
            )
            packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0
        }
    }
    
    /**
     * 检查是否有用户数据
     */
    private fun hasUserData(): Boolean {
        return try {
            val jsonObject = JSONObject(AllDataTool.dataState)
            val user = jsonObject.getString("q_j_u")
            !user.isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取用户代码
     */
    private fun getUserCode(): String {
        return try {
            val jsonObject = JSONObject(AllDataTool.dataState)
            jsonObject.getString("q_j_u")
        } catch (e: Exception) {
            ""
        }
    }
}

/**
 * 设备基础信息数据类
 */
data class DeviceInfo(
    val packageName: String,
    val appVersion: String,
    val distinctId: String,
    val logId: String,
    val timestamp: Long,
    val manufacturer: String,
    val deviceBrand: String,
    val osVersion: String,
    val androidId: String
)

/**
 * 安装信息数据类
 */
data class InstallInfo(
    val buildId: String,
    val referrerUrl: String,
    val firstInstallTime: Long
)

/**
 * 用户信息数据类
 */
data class UserInfo(
    val userCode: String
)
