package com.smoke.clears.away.single

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smoke.clears.away.single.databinding.SingleLiBinding


class LiSingle : AppCompatActivity() {
    private val binding by lazy { SingleLiBinding.inflate(layoutInflater) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用高阶函数配置EdgeToEdge
        SinglePageUtils.setupEdgeToEdge(this, binding.root, R.id.setting)
        // 设置点击监听器
        setupClickListeners()
    }


    private fun setupClickListeners() {
        // 使用高阶函数批量设置点击监听器
        SinglePageUtils.setMultipleClickListeners(
            binding.btnBack to { handleBackClick() },
            binding.itemPrivacy to { handlePrivacyClick() },
            binding.itemShare to { handleShareClick() }
        )
    }


    private fun handleBackClick() {
        finish()
    }


    private fun handlePrivacyClick() {
        // TODO
        SinglePageUtils.openWebPage(
            activity = this,
            url = "https://google-privacy-policy-url.com",
            onError = { exception ->
                // 错误处理（如果需要）
            }
        )
    }


    private fun handleShareClick() {
        // 使用高阶函数分享内容
        SinglePageUtils.shareContent(
            activity = this,
            subject = "Share app",
            text = "I found a great app, give it a try!",
            chooserTitle = "Share app"
        )
    }
}