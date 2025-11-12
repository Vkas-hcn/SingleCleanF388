package com.mastery.leaves.trace.ami

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object AllDataTool {

    private var application: Application? = null
    var isOpenNotification = false

    var getMainUser: Application
        get() = application ?: throw IllegalStateException("AllDataTool not initialized")
        set(value) {
            application = value
        }

    var stateIns: Boolean
        get() = prefs("vsdvce").getBoolean("stateIns", false)
        set(value) = prefs("vsdvce").edit().putBoolean("stateIns", value).apply()

    var showState: Boolean
        get() = prefs("sczcqw").getBoolean("showState", false)
        set(value) = prefs("sczcqw").edit().putBoolean("showState", value).apply()

    var idState: String
        get() = prefs("cvdsfva").getString("idState", "") ?: ""
        set(value) = prefs("cvdsfva").edit().putString("idState", value).apply()

    var refState: String
        get() = prefs("vfrewx").getString("refState", "") ?: ""
        set(value) = prefs("vfrewx").edit().putString("refState", value).apply()

    var dataState: String
        get() = prefs("kuyjHBd").getString("dataState", "") ?: ""
        set(value) = prefs("kuyjHBd").edit().putString("dataState", value).apply()

    //referrerClickTimestampSeconds
    var rone: String
        get() = prefs("csdcqwd").getString("rone", "") ?: ""
        set(value) = prefs("csdcqwd").edit().putString("rone", value).apply()

    //referrerClickTimestampServerSeconds
    var rtow: String
        get() = prefs("xecd").getString("rtow", "") ?: ""
        set(value) = prefs("xecd").edit().putString("rtow", value).apply()

    var rIns: String
        get() = prefs("dfvdsad").getString("rIns", "") ?: ""
        set(value) = prefs("dfvdsad").edit().putString("rIns", value).apply()

    private fun prefs(name: String): SharedPreferences {
        val app = application ?: throw IllegalStateException("AllDataTool not initialized")
        return app.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}