package com.smoke.clears.away.single

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.smoke.clears.away.single.databinding.SingleQingBinding

/**
 * QingSingle页面 - 引导页
 * 使用高阶函数实现倒计时跳转功能
 */
class QingSingle : AppCompatActivity() {
    private val binding by lazy { SingleQingBinding.inflate(layoutInflater) }
    private lateinit var countDownTimer: CountDownTimer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用高阶函数配置EdgeToEdge
        SinglePageUtils.setupEdgeToEdge(this, binding.root, R.id.guide)
        // 启动倒计时
        initializeCountdown()
    }
    
    /**
     * 初始化倒计时功能
     */
    private fun initializeCountdown() {
        // 使用高阶函数创建倒计时
        countDownTimer = SinglePageUtils.createCountDownTimer(
            duration = 2000L,
            interval = 100L,
            onTick = { millisUntilFinished ->
                // 倒计时中的更新逻辑（当前为空）
            },
            onFinish = {
                // 倒计时完成后跳转到HaiSingle页面
                navigateToNextPage()
            }
        )
        
        countDownTimer.start()
        
        // 使用高阶函数禁用返回按钮
        SinglePageUtils.disableBackPress(this) {
            // 返回按钮被禁用，不执行任何操作
        }
    }
    
    /**
     * 跳转到下一个页面
     */
    private fun navigateToNextPage() {
        SinglePageUtils.navigateToActivity(
            activity = this,
            targetClass = HaiSingle::class.java,
            finishCurrent = true
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // 确保在页面销毁时取消倒计时
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}