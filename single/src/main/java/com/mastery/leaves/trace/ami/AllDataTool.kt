package com.mastery.leaves.trace.ami

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object AllDataTool {
    var fcmString = "jqlait"
    var spKeyFile = "single"
    var kupaName = "com.smoke.clears.away.single.DcSingle"
    var isOpenNotification = false

    private var _getMainUser: Application? = null
    var getMainUser: Application
        get() = _getMainUser ?: throw IllegalStateException("AllDataTool not initialized")
        set(value) {
            _getMainUser = value
            cachedSharedPrefs = null
            onInitialized()
        }

    private fun onInitialized() {
        getSharedPrefs()
    }

    @Volatile
    private var cachedSharedPrefs: SharedPreferences? = null
    
    private fun getSharedPrefs(): SharedPreferences? {
        // 双重检查锁定模式
        cachedSharedPrefs?.let { return it }
        
        return try {
            if (_getMainUser != null) {
                synchronized(this) {
                    cachedSharedPrefs ?: run {
                        val prefs = _getMainUser!!.getSharedPreferences(spKeyFile, Context.MODE_PRIVATE)
                        cachedSharedPrefs = prefs
                        prefs
                    }
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    // 检查是否已初始化
    fun isInitialized(): Boolean = _getMainUser != null
    
    // 等待初始化完成后执行操作
    fun whenInitialized(action: () -> Unit) {
        if (isInitialized()) {
            action()
        } else {
            // 可以添加到待执行队列，或者直接忽略
            // 这里选择忽略，因为通常初始化会很快完成
        }
    }

    // 安全获取布尔值，确保初始化后才读取
    fun getSafeBooleanValue(key: String, defaultValue: Boolean = false): Boolean {
        return if (isInitialized()) {
            getSharedPrefs()?.getBoolean(key, defaultValue) ?: defaultValue
        } else {
            defaultValue
        }
    }
    
    // 安全设置布尔值
    fun setSafeBooleanValue(key: String, value: Boolean) {
        whenInitialized {
            getSharedPrefs()?.edit()?.putBoolean(key, value)?.apply()
        }
    }
    
    // 安全获取字符串值
    fun getSafeStringValue(key: String, defaultValue: String = ""): String {
        return if (isInitialized()) {
            getSharedPrefs()?.getString(key, defaultValue) ?: defaultValue
        } else {
            defaultValue
        }
    }
    
    // 安全设置字符串值
    fun setSafeStringValue(key: String, value: String) {
        whenInitialized {
            getSharedPrefs()?.edit()?.putString(key, value)?.apply()
        }
    }

    var stateIns: Boolean
        get() = getSafeBooleanValue("vsdvce", false)
        set(value) = setSafeBooleanValue("vsdvce", value)

    var fcmState: Boolean
        get() = getSafeBooleanValue("cwecsd", false)
        set(value) = setSafeBooleanValue("cwecsd", value)
    var showState: Boolean
        get() = getSafeBooleanValue("sczcqw", false)
        set(value) = setSafeBooleanValue("sczcqw", value)

    var idState: String
        get() = getSafeStringValue("cvdsfva", "")
        set(value) = setSafeStringValue("cvdsfva", value)

    var refState: String
        get() = getSafeStringValue("vfrewx", "")
        set(value) = setSafeStringValue("vfrewx", value)

    var dataState: String
        get() = getSafeStringValue("kuyjHBd", "")
        set(value) = setSafeStringValue("kuyjHBd", value)

    //referrerClickTimestampSeconds
    var rone: String
        get() = getSafeStringValue("csdcqwd", "")
        set(value) = setSafeStringValue("csdcqwd", value)

    //referrerClickTimestampServerSeconds
    var rtow: String
        get() = getSafeStringValue("xecd", "")
        set(value) = setSafeStringValue("xecd", value)

    private fun prefs(name: String): SharedPreferences {
        return getMainUser.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}