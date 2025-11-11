package com.smoke.clears.away.single

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.smoke.clears.away.single.databinding.SingleLiBinding

class LiSingle : AppCompatActivity() {
    val binding by lazy { SingleLiBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.itemPrivacy.setOnClickListener {
            //TODO 隐私政策页面
             val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google-privacy-policy-url.com"))
             startActivity(intent)
        }

        binding.itemShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Share app")
                putExtra(Intent.EXTRA_TEXT, "I found a great app, give it a try!")
            }
            startActivity(Intent.createChooser(shareIntent, "Share app"))
        }


    }
}