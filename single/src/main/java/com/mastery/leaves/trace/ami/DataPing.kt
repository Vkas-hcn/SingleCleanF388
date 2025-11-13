package com.mastery.leaves.trace.ami

import android.content.pm.PackageManager
import android.os.Build
import org.json.JSONObject
import java.util.UUID

object DataPing {
    private fun topJsonData(): JSONObject {
        return JSONObject().apply {
            //bundle_id
            put("epstein", AllDataTool.getMainUser.packageName)
            //os
            put("tramway", "monogamy")
            //app_version
            put("ising", DataPgTool.instance.showAppVersion())
            //distinct_id
            put("riotous", AllDataTool.idState)
            //log_id
            put("yaounde", UUID.randomUUID().toString())

            //client_ts
            put("peaceful", System.currentTimeMillis())

            //manufacturer
            put("chatham", Build.MANUFACTURER)

            //device_model-最新需要传真实值
            put("diabase", Build.BRAND)
            //os_version
            put("francis", Build.VERSION.RELEASE)
            //operator 传假值字符串
            put("pobox", "vsdw")
            //system_language//假值
            put("icy", "da_ed")
            //android_id
            put("flu", AllDataTool.idState)
            //gaid
            put("leibniz", "")
            if(isCanglobalevents()){
                put("russia", JSONObject().apply {
                    put("usercode", getCanglobalevents())
                })
            }
        }
    }

    fun upInstallJson(): String {
        return topJsonData().apply {
            //build
            put("annotate", "build/${Build.ID}")

            //referrer_url
            put("cathode", AllDataTool.refState)

            //user_agent
            put("wishy", "")

            //lat
            put("exxon", "seriatim")

            //referrer_click_timestamp_seconds
            put("ragging", 0)

            //install_begin_timestamp_seconds
            put("nikolai", 0)

            //referrer_click_timestamp_server_seconds
            put("gall", 0)

            //install_begin_timestamp_server_seconds
            put("selena", 0)

            //install_first_seconds
            put("mumble", getFirstInstallTime())

            //last_update_seconds
            put("avoid", 0)

            put("maul", "somehow")
        }.toString()
    }

    fun upAdJson(adJson: String): String {
        val adJsonObject = JSONObject(adJson)
        return topJsonData().apply {
            put("maul","ohio")
            val keys = adJsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                this.put(key, adJsonObject.get(key))
            }
        }.toString()
    }



    fun upPointJson(
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
    ): String {
        return topJsonData().apply {
            put("maul", name)
            if (key1 != null) {
                put("shoofly", JSONObject().apply {
                    put(key1, keyValue1)
                })
            }
        }.toString()
    }

    private fun getFirstInstallTime(): Long {
        try {
            val packageInfo =
                AllDataTool.getMainUser.packageManager.getPackageInfo(AllDataTool.getMainUser.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


    fun isCanglobalevents(): Boolean{
        try {
            val jsonObject = JSONObject(AllDataTool.dataState)
            val user = jsonObject.getString("q_j_u")
            return !user.isNullOrEmpty()
        } catch (e: Exception) {
            return false
        }
    }
    fun getCanglobalevents(): String{
        try {
            val jsonObject = JSONObject(AllDataTool.dataState)
            val user = jsonObject.getString("q_j_u")
            return user
        } catch (e: Exception) {
            return ""
        }
    }

}