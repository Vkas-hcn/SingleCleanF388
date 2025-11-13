package com.mastery.leaves.trace.dimoting.aligait

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.ami.ChongTool
import com.mastery.leaves.trace.core.CanNextGo
import org.json.JSONObject
import tool.sv.QnSer

class Ey : Application.ActivityLifecycleCallbacks {

    // 前台Activity数量
    private var num = 0

    // 保存所有存活的Activity
    private val activityStack = mutableListOf<Activity>()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStack.add(activity)
        oonn(activity)
        CanNextGo.showLog("onActivityCreated: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStarted(activity: Activity) {
        num++
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        num--
        if (num <= 0) {
            num = 0
            onAppEnteredBackground()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        activityStack.remove(activity)
    }


    fun onAppEnteredBackground() {
        runCatching {
            if (ChongTool.getAUTool(JSONObject(AllDataTool.dataState))) {
                val activitiesToFinish = activityStack.toList()
                activitiesToFinish.forEach { activity ->
                    if (!activity.isFinishing) {
                        activity.finishAndRemoveTask()
                    }
                }
            }
        }
    }

    private var lastOpenTime = 0L

    fun oonn(context: Context) {
        if (AllDataTool.isOpenNotification && System.currentTimeMillis() - lastOpenTime < 60000 * 10) return
        lastOpenTime = System.currentTimeMillis()
        runCatching {
            ContextCompat.startForegroundService(
                context,
                Intent(context, QnSer::class.java)
            )
        }
    }
}