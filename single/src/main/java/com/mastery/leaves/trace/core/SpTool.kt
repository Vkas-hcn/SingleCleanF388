package com.mastery.leaves.trace.core

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SpTool private constructor(context: Context, private val name: String) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: SpTool? = null

        fun getInstance(context: Context, name: String = "default"): SpTool {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SpTool(context.applicationContext, name).also { INSTANCE = it }
            }
        }
    }

    /**
     * 委托属性 - String类型
     */
    fun stringPreference(defaultValue: String = "") = object : ReadWriteProperty<Any?, String> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return sharedPreferences.getString(property.name, defaultValue) ?: defaultValue
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            sharedPreferences.edit().putString(property.name, value).apply()
        }
    }

    /**
     * 委托属性 - Int类型
     */
    fun intPreference(defaultValue: Int = 0) = object : ReadWriteProperty<Any?, Int> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            return sharedPreferences.getInt(property.name, defaultValue)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            sharedPreferences.edit().putInt(property.name, value).apply()
        }
    }

    /**
     * 委托属性 - Boolean类型
     */
    fun booleanPreference(defaultValue: Boolean = false) = object :
        ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return sharedPreferences.getBoolean(property.name, defaultValue)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            sharedPreferences.edit().putBoolean(property.name, value).apply()
        }
    }

    /**
     * 委托属性 - Float类型
     */
    fun floatPreference(defaultValue: Float = 0f) = object : ReadWriteProperty<Any?, Float> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Float {
            return sharedPreferences.getFloat(property.name, defaultValue)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
            sharedPreferences.edit().putFloat(property.name, value).apply()
        }
    }

    /**
     * 委托属性 - Long类型
     */
    fun longPreference(defaultValue: Long = 0L) = object : ReadWriteProperty<Any?, Long> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
            return sharedPreferences.getLong(property.name, defaultValue)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
            sharedPreferences.edit().putLong(property.name, value).apply()
        }
    }

    /**
     * 清除所有数据
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * 清除指定key的数据
     */
    fun clear(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}