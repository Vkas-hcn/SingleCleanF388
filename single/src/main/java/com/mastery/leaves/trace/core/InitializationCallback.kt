package com.mastery.leaves.trace.core

import android.app.Application

/**
 * 初始化回调接口
 */
interface InitializationCallback {
    fun onSuccess()
    fun onError(error: Throwable)
}

/**
 * 初始化组件接口
 */
interface InitializationComponent {
    fun initialize(app: Application, callback: InitializationCallback? = null)
    fun getComponentName(): String
}

/**
 * 初始化协调器接口
 */
interface InitializationCoordinator {
    fun addComponent(component: InitializationComponent)
    fun startInitialization(app: Application)
    fun setGlobalCallback(callback: InitializationCallback?)
}
