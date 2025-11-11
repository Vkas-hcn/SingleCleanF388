package com.smoke.clears.away.single

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.smoke.clears.away.single.databinding.SingleQingBinding

class QingSingle : AppCompatActivity() {
    val binding by lazy { SingleQingBinding.inflate(layoutInflater) }
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.guide)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        startCountdown()
    }
    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(2000, 100) { // 2秒，每100毫秒更新一次
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                val intent = Intent(this@QingSingle, HaiSingle::class.java)
                startActivity(intent)
                finish()
            }
        }

        countDownTimer.start()
        onBackPressedDispatcher.addCallback {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保在页面销毁时取消倒计时
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}