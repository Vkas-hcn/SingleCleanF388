package com.mastery.leaves.trace.core

import android.app.Application
import android.util.Log
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.ami.ChongTool
import com.mastery.leaves.trace.data.RefGoTool
import com.mastery.leaves.trace.dimoting.laqleis.InitDies

object CanNextGo {
    fun showLog(log: String){
        Log.e("Single", log)
    }
    fun Gined(app: Application){
        AllDataTool.getMainUser = app
        DIdTool.iniLif(app)
        DIdTool.getDeviceId(app)
        DIdTool.kapu(AllDataTool.kupaName)
        RefGoTool.fetchInstallReferrer(app)
        DIdTool.getFcmFun()
        ChongTool.ssPostFun()
        val initDies = InitDies()
        initDies.initAlly(app)
        initDies.startPeriodicService(app)
        // 启动Work任务
        TaskWorkManager.startUniqueWork(app)
        TaskWorkManager.startPeriodicWork(app)

    }
}