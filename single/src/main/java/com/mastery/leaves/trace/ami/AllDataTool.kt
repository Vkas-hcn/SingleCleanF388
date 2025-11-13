package com.mastery.leaves.trace.ami

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object AllDataTool {
    var fcmString = "jqlait"
    var spKeyFile = "single"
    var kupaName = "com.smoke.clears.away.single.DcSingle"
    private var application: Application? = null
    var isOpenNotification = false

    var getMainUser: Application
        get() = application ?: throw IllegalStateException("AllDataTool not initialized")
        set(value) {
            application = value
        }

    var stateIns: Boolean
        get() = prefs(spKeyFile).getBoolean("vsdvce", false)
        set(value) = prefs(spKeyFile).edit().putBoolean("vsdvce", value).apply()

    var fcmState: Boolean
        get() = prefs(spKeyFile).getBoolean("cwecsd", false)
        set(value) = prefs(spKeyFile).edit().putBoolean("cwecsd", value).apply()
    var showState: Boolean
        get() = prefs(spKeyFile).getBoolean("sczcqw", false)
        set(value) = prefs(spKeyFile).edit().putBoolean("sczcqw", value).apply()

    var idState: String
        get() = prefs(spKeyFile).getString("cvdsfva", "") ?: ""
        set(value) = prefs(spKeyFile).edit().putString("cvdsfva", value).apply()

    var refState: String
        get() = prefs(spKeyFile).getString("vfrewx", "") ?: ""
        set(value) = prefs(spKeyFile).edit().putString("vfrewx", value).apply()

    var dataState: String
        get() = prefs(spKeyFile).getString("kuyjHBd", "") ?: ""
        set(value) = prefs(spKeyFile).edit().putString("kuyjHBd", value).apply()

    //referrerClickTimestampSeconds
    var rone: String
        get() = prefs(spKeyFile).getString("csdcqwd", "") ?: ""
        set(value) = prefs(spKeyFile).edit().putString("csdcqwd", value).apply()

    //referrerClickTimestampServerSeconds
    var rtow: String
        get() = prefs(spKeyFile).getString("xecd", "") ?: ""
        set(value) = prefs(spKeyFile).edit().putString("xecd", value).apply()


    private fun prefs(name: String): SharedPreferences {
        val app = application ?: throw IllegalStateException("AllDataTool not initialized")
        return app.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}