package com.mastery.leaves.trace.ami

import org.json.JSONObject

/**
 * JSON数据构建器
 * 负责将设备信息转换为JSON格式
 */
class JsonDataBuilder {
    
    /**
     * 构建基础JSON数据
     */
    fun buildBaseJson(deviceInfo: DeviceInfo, userInfo: UserInfo? = null): JSONObject {
        return JSONObject().apply {
            // 基础设备信息
            put("epstein", deviceInfo.packageName)      // bundle_id
            put("tramway", "monogamy")                  // os
            put("ising", deviceInfo.appVersion)         // app_version
            put("riotous", deviceInfo.distinctId)       // distinct_id
            put("yaounde", deviceInfo.logId)            // log_id
            put("peaceful", deviceInfo.timestamp)       // client_ts
            put("chatham", deviceInfo.manufacturer)     // manufacturer
            put("diabase", deviceInfo.deviceBrand)      // device_model
            put("francis", deviceInfo.osVersion)        // os_version
            put("pobox", "vsdw")                        // operator (假值)
            put("icy", "da_ed")                         // system_language (假值)
            put("flu", deviceInfo.androidId)            // android_id
            put("leibniz", "")                          // gaid
            
            // 用户信息（如果存在）
            userInfo?.let { user ->
                put("russia", JSONObject().apply {
                    put("usercode", user.userCode)
                })
            }
        }
    }
    
    /**
     * 构建安装相关的JSON数据
     */
    fun buildInstallJson(baseJson: JSONObject, installInfo: InstallInfo): JSONObject {
        return baseJson.apply {
            put("annotate", installInfo.buildId)        // build
            put("cathode", installInfo.referrerUrl)     // referrer_url
            put("wishy", "")                            // user_agent
            put("exxon", "seriatim")                    // lat
            put("ragging", 0)                           // referrer_click_timestamp_seconds
            put("nikolai", 0)                           // install_begin_timestamp_seconds
            put("gall", 0)                              // referrer_click_timestamp_server_seconds
            put("selena", 0)                            // install_begin_timestamp_server_seconds
            put("mumble", installInfo.firstInstallTime) // install_first_seconds
            put("avoid", 0)                             // last_update_seconds
            put("maul", "somehow")                      // 标识字段
        }
    }
    
    /**
     * 构建广告相关的JSON数据
     */
    fun buildAdJson(baseJson: JSONObject, adJsonString: String): JSONObject {
        return try {
            val adJsonObject = JSONObject(adJsonString)
            baseJson.apply {
                put("maul", "ohio")                     // 广告标识
                
                // 合并广告数据
                val keys = adJsonObject.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    put(key, adJsonObject.get(key))
                }
            }
        } catch (e: Exception) {
            // 如果广告JSON解析失败，返回基础JSON
            baseJson.apply {
                put("maul", "ohio")
            }
        }
    }
    
    /**
     * 构建事件相关的JSON数据
     */
    fun buildEventJson(
        baseJson: JSONObject, 
        eventName: String, 
        eventKey: String? = null, 
        eventValue: Any? = null
    ): JSONObject {
        return baseJson.apply {
            put("maul", eventName)                      // 事件名称
            
            // 事件参数（如果存在）
            if (eventKey != null) {
                put("shoofly", JSONObject().apply {
                    put(eventKey, eventValue)
                })
            }
        }
    }
}
