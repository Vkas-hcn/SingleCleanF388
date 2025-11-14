package com.mastery.leaves.trace.device

import android.app.Application
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.core.CanNextGo
import com.mastery.leaves.trace.core.DIdTool
import com.mastery.leaves.trace.core.InitializationCallback
import com.mastery.leaves.trace.core.InitializationComponent
import com.mastery.leaves.trace.data.RefGoTool

/**
 * 设备相关初始化组件
 */
class DeviceInitializer : InitializationComponent {
    
    override fun initialize(app: Application, callback: InitializationCallback?) {
        try {

            // 基础设置
            AllDataTool.getMainUser = app
            
            // 生命周期初始化
            DIdTool.iniLif(app)
            
            // 设备ID获取
            DIdTool.getDeviceId(app)
            
            // 安装来源获取
            RefGoTool.fetchInstallReferrer(app)
            
            callback?.onSuccess()
            
        } catch (e: Exception) {
            CanNextGo.showLog("DeviceInitializer: 设备初始化失败 - ${e.message}")
            callback?.onError(e)
        }
    }
    
    override fun getComponentName(): String = "DeviceInitializer"
}
