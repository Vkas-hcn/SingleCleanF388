package com.mastery.leaves.trace.ami

import org.json.JSONObject

/**
 * 数据格式化器
 * 负责将JSON数据格式化为最终的字符串格式
 */
class DataFormatter {
    
    private val deviceInfoCollector = DeviceInfoCollector()
    private val jsonDataBuilder = JsonDataBuilder()
    
    /**
     * 格式化安装数据
     */
    fun formatInstallData(): String {
        return try {
            val deviceInfo = deviceInfoCollector.getBaseDeviceInfo()
            val installInfo = deviceInfoCollector.getInstallInfo()
            val userInfo = deviceInfoCollector.getUserInfo()
            
            val baseJson = jsonDataBuilder.buildBaseJson(deviceInfo, userInfo)
            val installJson = jsonDataBuilder.buildInstallJson(baseJson, installInfo)
            
            installJson.toString()
        } catch (e: Exception) {
            // 发生异常时返回空JSON，避免崩溃
            JSONObject().toString()
        }
    }
    
    /**
     * 格式化广告数据
     */
    fun formatAdData(adJsonString: String): String {
        return try {
            val deviceInfo = deviceInfoCollector.getBaseDeviceInfo()
            val userInfo = deviceInfoCollector.getUserInfo()
            
            val baseJson = jsonDataBuilder.buildBaseJson(deviceInfo, userInfo)
            val adJson = jsonDataBuilder.buildAdJson(baseJson, adJsonString)
            
            adJson.toString()
        } catch (e: Exception) {
            // 发生异常时返回空JSON，避免崩溃
            JSONObject().toString()
        }
    }
    
    /**
     * 格式化事件数据
     */
    fun formatEventData(
        eventName: String,
        eventKey: String? = null,
        eventValue: Any? = null
    ): String {
        return try {
            val deviceInfo = deviceInfoCollector.getBaseDeviceInfo()
            val userInfo = deviceInfoCollector.getUserInfo()
            
            val baseJson = jsonDataBuilder.buildBaseJson(deviceInfo, userInfo)
            val eventJson = jsonDataBuilder.buildEventJson(baseJson, eventName, eventKey, eventValue)
            
            eventJson.toString()
        } catch (e: Exception) {
            // 发生异常时返回空JSON，避免崩溃
            JSONObject().toString()
        }
    }
    
    /**
     * 格式化自定义数据
     */
    fun formatCustomData(customizer: (JSONObject) -> JSONObject): String {
        return try {
            val deviceInfo = deviceInfoCollector.getBaseDeviceInfo()
            val userInfo = deviceInfoCollector.getUserInfo()
            
            val baseJson = jsonDataBuilder.buildBaseJson(deviceInfo, userInfo)
            val customJson = customizer(baseJson)
            
            customJson.toString()
        } catch (e: Exception) {
            // 发生异常时返回空JSON，避免崩溃
            JSONObject().toString()
        }
    }
}
