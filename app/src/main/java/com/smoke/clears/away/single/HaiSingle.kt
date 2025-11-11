package com.smoke.clears.away.single

import android.annotation.SuppressLint
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
// StorageStatsManager会在运行时根据API级别检查
import android.provider.Settings
import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.text.DecimalFormat
import java.util.*
import kotlin.math.max

class HaiSingle : AppCompatActivity() {
    
    // 声明视图变量
    private lateinit var tvStorageUsed: TextView
    private lateinit var tvStorageTotal: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ivSetting: ImageView
    private lateinit var btnSmartClean: AppCompatTextView
    private lateinit var llCpu: LinearLayout
    private lateinit var llBattery: LinearLayout
    
    // 对话框相关视图
    private lateinit var llDialog: ConstraintLayout
    private lateinit var btnCancel: Button
    private lateinit var btnYes: Button
    
    // 权限请求码
    private val PERMISSION_REQUEST_CODE = 1001
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.single_hai)
        
        // 初始化视图
        initViews()
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // 初始化存储信息
        initStorageInfo()
        
        // 设置点击事件
        setClickListeners()
    }
    
    /**
     * 初始化视图
     */
    private fun initViews() {
        tvStorageUsed = findViewById(R.id.tv_storage_used)
        tvStorageTotal = findViewById(R.id.tv_storage_total)
        progressBar = findViewById(R.id.progress_bar)
        ivSetting = findViewById(R.id.iv_setting)
        btnSmartClean = findViewById(R.id.btn_smart_clean)
        llCpu = findViewById(R.id.ll_cpu)
        llBattery = findViewById(R.id.ll_battery)
        
        // 初始化对话框相关视图
        llDialog = findViewById(R.id.ll_dialog)
        btnCancel = findViewById(R.id.btn_cancel)
        btnYes = findViewById(R.id.btn_yes)
    }
    
    /**
     * 初始化存储信息
     */
    private fun initStorageInfo() {
        updateStorageInfo()
    }
    
    @SuppressLint("NewApi")
    private fun updateStorageInfo() {
        try {
            val internalStat = StatFs(Environment.getDataDirectory().path)

            val blockSize = internalStat.blockSizeLong
            val totalBlocks = internalStat.blockCountLong
            val availableBlocks = internalStat.availableBlocksLong

            val totalUserBytes = totalBlocks * blockSize  // 用户可见的总空间
            val availableBytes = availableBlocks * blockSize  // 用户可用空间
            val actualTotalBytes = getTotalDeviceStorageAccurate()
            val displayTotalBytes = max(actualTotalBytes, totalUserBytes)
            val displayFreeBytes = availableBytes
            val displayUsedBytes = displayTotalBytes - displayFreeBytes

            val usedStorageFormatted = formatStorageSize(displayUsedBytes)
            val totalStorageFormatted = formatStorageSize(displayTotalBytes)

            tvStorageUsed.text = usedStorageFormatted.first
            tvStorageTotal.text = "/${totalStorageFormatted.first}"

            // 计算并设置进度条进度
            val progress = ((displayUsedBytes.toFloat() / displayTotalBytes) * 100).toInt()
            progressBar.progress = progress

        } catch (e: Exception) {
            e.printStackTrace()
            tvStorageUsed.text = "-- GB"
            tvStorageTotal.text = "/-- GB"
        }
    }

    private fun getTotalDeviceStorageAccurate(): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val storageStatsManager =
                    getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                return storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
            }

            val internalStat = StatFs(Environment.getDataDirectory().path)

            val internalTotal = internalStat.blockCountLong * internalStat.blockSizeLong

            val storagePaths = arrayOf(
                Environment.getRootDirectory().absolutePath,      // /system
                Environment.getDataDirectory().absolutePath,      // /data
                Environment.getDownloadCacheDirectory().absolutePath // /cache
            )

            var total: Long = 0
            for (path in storagePaths) {
                val stat = StatFs(path)
                val blockSize = stat.blockSizeLong
                val blockCount = stat.blockCountLong
                total += blockSize * blockCount
            }

            val withSystemOverhead = total + (total * 0.07).toLong()

            max(internalTotal, withSystemOverhead)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val internalStat = StatFs(Environment.getDataDirectory().path)
                val internalTotal = internalStat.blockCountLong * internalStat.blockSizeLong
                internalTotal + (internalTotal * 0.12).toLong()
            } catch (innerException: Exception) {
                innerException.printStackTrace()
                0L
            }
        }
    }
    
    /**
     * 检查存储权限
     */
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11及以上，检查是否有MANAGE_EXTERNAL_STORAGE权限
            if (Environment.isExternalStorageManager()) {
                // 已有权限，执行清理操作
                performSmartClean()
            } else {
                // 显示授权对话框
                showPermissionDialog()
            }
        } else {
            // Android 10及以下，检查读写存储权限
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 已有权限，执行清理操作
                performSmartClean()
            } else {
                // 显示授权对话框
                showPermissionDialog()
            }
        }
    }
    
    /**
     * 显示权限请求对话框
     */
    private fun showPermissionDialog() {
        llDialog.visibility = View.VISIBLE
    }
    
    /**
     * 请求存储权限
     */
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11及以上，跳转到系统设置页面请求MANAGE_EXTERNAL_STORAGE权限
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            }
        } else {
            // Android 10及以下，请求读写存储权限
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    /**
     * 处理权限请求结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 权限被授予，执行清理操作
                performSmartClean()
            }
        }
    }
    
    /**
     * 处理Activity结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // 检查权限是否被授予
                if (Environment.isExternalStorageManager()) {
                    performSmartClean()
                }
            }
        }
    }
    
    /**
     * 执行智能清理功能
     */
    private fun performSmartClean() {
        startActivity(Intent(this, PiSingle::class.java))
    }

    private fun formatStorageSize(bytes: Long): Pair<String, String> {
        return when {
            bytes >= 1000L * 1000L * 1000L -> {
                val gb = bytes.toDouble() / (1000L * 1000L * 1000L)
                val formatted = if (gb >= 10.0) {
                    DecimalFormat("#").format(gb)
                } else {
                    DecimalFormat("#.#").format(gb)
                }
                Pair("$formatted GB", "GB")
            }
            bytes >= 1000L * 1000L -> {
                val mb = bytes.toDouble() / (1000L * 1000L)
                val formatted = if (mb >= 10.0) {
                    DecimalFormat("#").format(mb)
                } else {
                    DecimalFormat("#.#").format(mb)
                }
                Pair("$formatted MB", "MB")
            }
            else -> {
                Pair("0 MB", "MB")
            }
        }
    }
    

    private fun setClickListeners() {
        ivSetting.setOnClickListener {
            startActivity(Intent(this, LiSingle::class.java))
        }
        
        btnSmartClean.setOnClickListener {
            checkStoragePermission()
        }
        
        btnCancel.setOnClickListener {
            llDialog.visibility = View.GONE
        }
        
        btnYes.setOnClickListener {
            llDialog.visibility = View.GONE
            requestStoragePermission()
        }
        
        llCpu.setOnClickListener {
           startActivity(Intent(this, TanSingle::class.java))
        }
        
        llBattery.setOnClickListener {
            startActivity(Intent(this, DanSingle::class.java))

        }
        llDialog.setOnClickListener {
        }

    }
    

}