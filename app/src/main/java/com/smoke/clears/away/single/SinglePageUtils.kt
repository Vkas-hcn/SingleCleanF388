package com.smoke.clears.away.single

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.os.Process
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


object SinglePageUtils {


    fun setupEdgeToEdge(activity: AppCompatActivity, rootView: View, targetViewId: Int) {
        activity.enableEdgeToEdge()
        activity.setContentView(rootView)
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(targetViewId)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun createCountDownTimer(
        duration: Long,
        interval: Long,
        onTick: (millisUntilFinished: Long) -> Unit = {},
        onFinish: () -> Unit
    ): CountDownTimer {
        return object : CountDownTimer(duration, interval) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                onFinish()
            }
        }
    }


    fun disableBackPress(activity: AppCompatActivity, onBackPressed: () -> Unit = {}) {
        activity.onBackPressedDispatcher.addCallback(activity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        })
    }


    fun navigateToActivity(
        activity: Activity,
        targetClass: Class<*>,
        finishCurrent: Boolean = false,
        intentConfig: (Intent) -> Unit = {}
    ) {
        val intent = Intent(activity, targetClass)
        intentConfig(intent)
        activity.startActivity(intent)
        if (finishCurrent) {
            activity.finish()
        }
    }


    fun openWebPage(
        activity: Activity,
        url: String,
        onError: (Exception) -> Unit = {}
    ) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(intent)
        } catch (e: Exception) {
            onError(e)
        }
    }


    fun shareContent(
        activity: Activity,
        subject: String,
        text: String,
        chooserTitle: String = "Share"
    ) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        activity.startActivity(Intent.createChooser(shareIntent, chooserTitle))
    }


    inline fun setClickListener(view: View, crossinline onClick: () -> Unit) {
        view.setOnClickListener { onClick() }
    }

    fun setMultipleClickListeners(vararg viewActions: Pair<View, () -> Unit>) {
        viewActions.forEach { (view, action) ->
            view.setOnClickListener { action() }
        }
    }
    fun Context.getCurrentProcessName(): String? {
        val pid = Process.myPid()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        return activityManager?.runningAppProcesses
            ?.firstOrNull { it.pid == pid }
            ?.processName
    }


    fun Context.isMainProcess(): Boolean {
        return packageName == getCurrentProcessName()
    }
}
