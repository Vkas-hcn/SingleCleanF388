package com.mastery.leaves.trace.network

import android.app.Application
import com.mastery.leaves.trace.ami.ChongTool
import com.mastery.leaves.trace.core.CanNextGo
import com.mastery.leaves.trace.core.DIdTool
import com.mastery.leaves.trace.core.InitializationCallback
import com.mastery.leaves.trace.core.InitializationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 网络相关初始化组件
 */
class NetworkInitializer : InitializationComponent {
    
    override fun initialize(app: Application, callback: InitializationCallback?) {

        // 异步执行网络相关初始化
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // FCM初始化
                initializeFcm()
                
                // Post功能初始化
                initializePost()
                
                withContext(Dispatchers.Main) {
                    callback?.onSuccess()
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback?.onError(e)
                }
            }
        }
    }
    
    private fun initializeFcm() {
        DIdTool.getFcmFun()
    }
    
    private fun initializePost() {
        ChongTool.ssPostFun()
    }
    
    override fun getComponentName(): String = "NetworkInitializer"
}
