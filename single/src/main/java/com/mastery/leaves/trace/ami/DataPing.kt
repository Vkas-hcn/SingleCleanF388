package com.mastery.leaves.trace.ami

import org.json.JSONObject

/**
 * 数据上报JSON生成器 - 重构为模块化架构
 * 保持原有功能100%一致，但代码结构更清晰易懂
 * 
 * 重构特点：
 * 1. 职责分离：设备信息收集、JSON构建、数据格式化分别处理
 * 2. 易于理解：每个类都有明确的职责和清晰的方法名
 * 3. 异常安全：添加完善的异常处理，避免崩溃
 * 4. 功能一致：保持所有原有方法的行为和返回值完全一致
 */
object DataPing {
    
    // 使用数据格式化器来处理所有JSON生成逻辑
    private val dataFormatter = DataFormatter()
    
    /**
     * 生成安装相关的JSON数据
     * 对应原来的 upInstallJson() 方法
     */
    fun upInstallJson(): String {
        return dataFormatter.formatInstallData()
    }
    
    /**
     * 生成广告相关的JSON数据
     * 对应原来的 upAdJson(adJson: String) 方法
     */
    fun upAdJson(adJson: String): String {
        return dataFormatter.formatAdData(adJson)
    }
    
    /**
     * 生成事件相关的JSON数据
     * 对应原来的 upPointJson() 方法
     */
    fun upPointJson(
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
    ): String {
        return dataFormatter.formatEventData(name, key1, keyValue1)
    }
    
    /**
     * 检查是否有全局事件数据
     * 保持原有方法，向后兼容
     */
    fun isCanglobalevents(): Boolean {
        return try {
            val jsonObject = JSONObject(AllDataTool.dataState)
            val user = jsonObject.getString("q_j_u")
            !user.isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取全局事件数据
     * 保持原有方法，向后兼容
     */
    fun getCanglobalevents(): String {
        return try {
            val jsonObject = JSONObject(AllDataTool.dataState)
            jsonObject.getString("q_j_u")
        } catch (e: Exception) {
            ""
        }
    }
}