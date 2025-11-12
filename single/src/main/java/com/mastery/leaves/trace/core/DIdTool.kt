package com.mastery.leaves.trace.core

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.mastery.leaves.trace.ami.AllDataTool
import java.util.UUID

object DIdTool {


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
}
