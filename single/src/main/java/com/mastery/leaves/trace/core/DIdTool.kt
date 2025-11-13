package com.mastery.leaves.trace.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.dimoting.aligait.Ey
import java.util.UUID

object DIdTool {
    lateinit var ey: Ey
    fun iniLif(app: Application){
        ey = Ey()
        app.registerActivityLifecycleCallbacks(ey)
    }
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context) {
        if (AllDataTool.idState.isNotEmpty()) {
            return
        }
        AllDataTool.idState = try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )

            if (!androidId.isNullOrEmpty() && androidId != "9774d56d682e549c") {
                androidId
            } else {
                generateUUID()
            }
        } catch (e: Exception) {
            generateUUID()
        }
    }


    private fun generateUUID(): String {
        return try {
            UUID.randomUUID().toString()
        } catch (e: Exception) {
            // 极端情况下UUID生成失败，返回固定标识
            "unknown_device_id_${System.currentTimeMillis()}"
        }
    }

    fun getFcmFun() {
        if (!AllDataTool.fcmState) {
            runCatching {
                Firebase.messaging.subscribeToTopic(AllDataTool.fcmString)
                    .addOnSuccessListener {
                        AllDataTool.fcmState = true
                    }
                    .addOnFailureListener {
                    }
            }
        }
    }

    fun kapu(pa: String) {
        try {
            if (AllDataTool.showState) {
                return
            }
            val pm =  AllDataTool.getMainUser.packageManager
            val componentName = ComponentName( AllDataTool.getMainUser, pa)
            pm.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            CanNextGo.showLog("d2: -go")
            AllDataTool.showState = true
        } catch (e: Exception) {
            CanNextGo.showLog("Error in d2: " + e.message)
            e.printStackTrace()
        }
    }
}
