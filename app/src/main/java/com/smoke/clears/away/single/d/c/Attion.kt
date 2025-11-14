package com.smoke.clears.away.single.d.c

import android.app.Application
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.mastery.leaves.trace.core.CanNextGo
import com.smoke.clears.away.single.SinglePageUtils.isMainProcess

class Attion: Application() {
    override fun onCreate() {
        super.onCreate()
        if (isMainProcess()) {
            Log.e("TAG", "onCreate: Application", )
            CanNextGo.Gined(this)
        } else {
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    WebView.setDataDirectorySuffix(
                        getProcessName() ?: "default"
                    )
                }
            }
        }
    }


}