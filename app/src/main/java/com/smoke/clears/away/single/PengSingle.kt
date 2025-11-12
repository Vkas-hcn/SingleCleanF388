package com.smoke.clears.away.single


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smoke.clears.away.single.databinding.SinglePengBinding

class PengSingle : AppCompatActivity() {
    private lateinit var binding: SinglePengBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SinglePengBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val deletedCount = intent.getIntExtra("deleted_count", 0)
        val deletedSize = intent.getLongExtra("deleted_size", 0)
        setupViews(deletedCount, deletedSize)
    }




    private fun setupViews(deletedCount: Int, deletedSize: Long) {
        binding.tvSaveData.text = "Saved ${formatFileSize(deletedSize)} space for you"
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.mbCpu.setOnClickListener {
            startActivity(Intent(this, TanSingle::class.java))
            finish()
        }
        binding.mbBattery.setOnClickListener {
            startActivity(Intent(this, DanSingle::class.java))
            finish()
        }
        binding.mbClean.setOnClickListener {
            startActivity(Intent(this, PiSingle::class.java))
            finish()
        }
    }

    private fun formatFileSize(size: Long): String {
        return when {
            size >= 1000 * 1000 * 1000 -> String.format("%.2fGB", size / (1000.0 * 1000.0 * 1000.0))
            size >= 1000 * 1000 -> String.format("%.2fMB", size / (1000.0 * 1000.0))
            else -> String.format("%.2fKB", size / 1000.0)
        }
    }
}