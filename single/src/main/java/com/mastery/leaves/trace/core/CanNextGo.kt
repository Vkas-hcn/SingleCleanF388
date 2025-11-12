package com.mastery.leaves.trace.core

import android.app.Application
import android.util.Log
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.data.RefGoTool

object CanNextGo {
    fun showLog(log: String){
        Log.e("TAG", "log: $log")
    }
    fun Gined(app: Application){
        AllDataTool.getMainUser = app
        DIdTool.getDeviceId(app)
        RefGoTool.fetchInstallReferrer(app)
    }
}