package com.smoke.clears.away.single

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.smoke.clears.away.single.databinding.SinglePiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList


class PiSingle : AppCompatActivity() {
    private lateinit var binding: SinglePiBinding
    private val trashCategories = mutableListOf<TrashCategory>()
    private var totalTrashSize = 0L
    private val isScanning = AtomicBoolean(false)
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val categoryAdapter: CategoryAdapter by lazy {
        // use existing CategoryAdapter class name to preserve usages in layout
        CategoryAdapter(trashCategories) { updateCleanButtonState() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = SinglePiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pi)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.supportActionBar?.hide()
        setupViews()
        startScanning()
    }
    private fun setupViews() {
        binding.inClean.inClean.setOnClickListener {}
        binding.inClean.imgBack.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this@PiSingle)
            adapter = categoryAdapter
        }

        binding.btnCleanNow.setOnClickListener { cleanSelectedFiles() }

        binding.progressScaning.visibility = View.GONE
        binding.btnCleanNow.visibility = View.GONE
        updateTrashSize(0L)
    }

    private fun startScanning() {
        if (!isScanning.compareAndSet(false, true)) return

        binding.progressScaning.visibility = View.VISIBLE
        binding.progressScaning.progress = 0
        binding.btnCleanNow.visibility = View.GONE
        totalTrashSize = 0L

        initializeCategories()

        lifecycleScope.launch {
                scanForTrashFilesSuspend { path ->
                 { binding.tvScanningPath.text = "Scanning: $path" }
                }
            finishScanning()
            isScanning.set(false)
        }
    }


    private fun initializeCategories() {
        trashCategories.clear()
        trashCategories.addAll(listOf(
            TrashCategory(
                "App Cache",
                R.drawable.icon_cache,
                mutableListOf(),
                TrashType.APP_CACHE
            ),
            TrashCategory(
                "Apk Files",
                R.drawable.icon_apk,
                mutableListOf(),
                TrashType.APK_FILES
            ),
            TrashCategory(
                "Log Files",
                R.drawable.icon_log_files,
                mutableListOf(),
                TrashType.LOG_FILES
            ),
            TrashCategory(
                "AD Junk",
                R.drawable.icon_ad_junk,
                mutableListOf(),
                TrashType.ADJUNK
            ),
            TrashCategory(
                "Temp Files",
                R.drawable.icon_temp_files,
                mutableListOf(),
                TrashType.TEMP_FILES
            ),
         
        ))

        runOnUiThread {
            categoryAdapter.notifyDataSetChanged()
        }
    }

    private fun scanForTrashFiles() {
        // kept for binary compatibility, scanning is done in scanForTrashFilesSuspend
    }

    private suspend fun scanForTrashFilesSuspend(onPath: (String) -> Unit) {
        val rootDirs = mutableListOf<File>()
        Environment.getExternalStorageDirectory()?.let { rootDirs.add(it) }
        externalCacheDir?.let { rootDirs.add(it) }
        cacheDir?.let { rootDirs.add(it) }

        val commonTrashDirs = listOf(
            "/storage/emulated/0/Android/data",
            "/storage/emulated/0/Download",
            "/storage/emulated/0/Pictures/.thumbnails",
            "/storage/emulated/0/DCIM/.thumbnails",
            "/storage/emulated/0/.android_secure",
            "/storage/emulated/0/Documents"
        )

        commonTrashDirs.forEach { path ->
            val dir = File(path)
            if (dir.exists() && dir.canRead()) rootDirs.add(dir)
        }

        var progress = 0
        val totalDirs = rootDirs.size.coerceAtLeast(1)

        for (rootDir in rootDirs) {
            if (!isScanning.get()) return
            onPath(rootDir.absolutePath)
            try {
                scanDirectorySuspend(rootDir, 0) { trashFile ->
                    addTrashFile(trashFile)
                }
            } catch (ignored: Exception) {
            }
            progress++
            val progressPercent = (progress * 100) / totalDirs
            withContext(Dispatchers.Main) { binding.progressScaning.progress = progressPercent }
            // small throttle to avoid UI thrashing
            delay(200)
        }
    }

    private fun scanDirectory(dir: File, depth: Int) {
        if (depth > 4) return
        try {
            val files = dir.listFiles() ?: return
            for (file in files) {
                if (!isScanning.get()) return
                when {
                    file.isDirectory -> {
                        val skipDirs = arrayOf("proc", "sys", "dev", "system", "root")
                        if (!skipDirs.any { file.name.contains(it, true) }) scanDirectory(file, depth + 1)
                    }
                    file.isFile -> {
                        val trashFile = categorizeFile(file)
                        if (trashFile != null) {
                            addTrashFile(trashFile)
                            if (totalTrashSize > 500 * 1000 * 1000) return
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            // 忽略无权限访问的目录
        } catch (e: Exception) {
            // 忽略其他异常
        }
    }

    private suspend fun scanDirectorySuspend(dir: File, depth: Int, onFound: (TrashFile) -> Unit) {
        if (depth > 4) return
        val files = runCatching { dir.listFiles() }.getOrNull() ?: return
        for (file in files) {
            if (!isScanning.get()) return
            if (file.isDirectory) {
                val skipDirs = arrayOf("proc", "sys", "dev", "system", "root")
                if (!skipDirs.any { file.name.contains(it, true) }) scanDirectorySuspend(file, depth + 1, onFound)
            } else if (file.isFile) {
                val trashFile = categorizeFile(file)
                if (trashFile != null) {
                    onFound(trashFile)
                    if (totalTrashSize > 500 * 1000 * 1000) return
                }
            }
        }
    }

    private fun categorizeFile(file: File): TrashFile? {
        val fileName = file.name.lowercase()
        val filePath = file.absolutePath.lowercase()
        val fileSize = file.length()

        if (fileSize < 100) return null // 只过滤掉非常小的文件

        val type = when {
            filePath.contains("/cache/") ||
                    fileName.endsWith(".cache") ||
                    fileName.contains("cache") ||
                    filePath.contains("/app_cache/") ||
                    filePath.contains("/webview/") ||
                    fileName.endsWith(".dex") && filePath.contains("cache") -> TrashType.APP_CACHE

            fileName.endsWith(".apk") ||
                    fileName.endsWith(".xapk") ||
                    fileName.endsWith(".apks") -> TrashType.APK_FILES

            fileName.endsWith(".log") ||
                    fileName.endsWith(".txt") && (filePath.contains("log") || fileName.contains("log")) ||
                    fileName.endsWith(".crash") ||
                    fileName.startsWith("log") ||
                    filePath.contains("/logs/") -> TrashType.LOG_FILES

            fileName.endsWith(".tmp") ||
                    fileName.endsWith(".temp") ||
                    filePath.contains("/temp/") ||
                    filePath.contains("/.temp") ||
                    fileName.startsWith("tmp") ||
                    fileName.startsWith("temp") ||
                    filePath.contains("/temporary/") ||
                    filePath.contains("/.thumbnails/") -> TrashType.TEMP_FILES

            fileName.endsWith(".bak") ||
                    fileName.endsWith(".old") ||
                    fileName.startsWith("~") ||
                    fileName.contains("backup") ||
                    fileName.endsWith(".swp") ||
                    fileName.endsWith(".swo") ||
                    fileName.startsWith(".") && fileName.length > 10 ||
                    filePath.contains("/trash/") ||
                    filePath.contains("/recycle/") -> TrashType.ADJUNK

            fileSize > 10 * 1000 * 1000 && filePath.contains("/download") -> TrashType.ADJUNK

            else -> null
        }

        return if (type != null) {
            TrashFile(file.name, file.absolutePath, fileSize, false, type)
        } else null
    }

    private fun addTrashFile(trashFile: TrashFile) {
        val category = trashCategories.find { it.type == trashFile.type }
        category?.files?.add(trashFile)

        totalTrashSize += trashFile.size

        runOnUiThread {
            updateTrashSize(totalTrashSize)
            if (totalTrashSize > 0) binding.root.setBackgroundResource(R.drawable.bg_lj)
            categoryAdapter.notifyDataSetChanged()
        }
    }

    private fun updateTrashSize(size: Long) {
        val (displaySize, unit) = formatFileSize(size)
        binding.tvScannedSize.text = displaySize
        binding.tvScannedSizeUn.text = unit
    }

    private fun formatFileSize(size: Long): Pair<String, String> {
        return when {
            size >= 1000 * 1000 * 1000 -> {
                Pair(String.format("%.1f", size / (1000.0 * 1000.0 * 1000.0)), "GB")
            }
            size >= 1000 * 1000 -> {
                Pair(String.format("%.1f", size / (1000.0 * 1000.0)), "MB")
            }
            else -> {
                Pair(String.format("%.1f", size / 1000.0), "KB")
            }
        }
    }

    private fun finishScanning() {
        isScanning.set(false)
        binding.progressScaning.visibility = View.GONE
        binding.tvScanningPath.text = "Scan completed"

        trashCategories.forEach { category ->
            category.totalSize = category.files.sumOf { it.size }
        }
        categoryAdapter.notifyDataSetChanged()

        if (trashCategories.isNotEmpty()) {
            binding.btnCleanNow.visibility = View.VISIBLE
            trashCategories.forEach { category ->
                category.files.forEach { file ->
                    file.isSelected = true
                }
                category.isSelected = true
            }
            updateCleanButtonState()
        } else {
            binding.tvScanningPath.text = "No trash files found"
        }
    }

    private fun updateCleanButtonState() {
        val hasSelectedFiles = trashCategories.any { category ->
            category.files.any { it.isSelected }
        }
        binding.btnCleanNow.isEnabled = hasSelectedFiles
    }

    private fun cleanSelectedFiles() {
        val selectedFiles = mutableListOf<TrashFile>()
        trashCategories.forEach { category ->
            selectedFiles.addAll(category.files.filter { it.isSelected })
        }

        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, "Please select the file to clean", Toast.LENGTH_SHORT).show()
            return
        }

        // 显示in_clean页面
        binding.inClean.inClean.visibility = View.VISIBLE
        
        // 启动img_load图片旋转动画
        val imgLoad = binding.inClean.inClean.findViewById<ImageView>(R.id.img_load)
        val rotationAnimator = ObjectAnimator.ofFloat(imgLoad, "rotation", 0f, 360f).apply {
            duration = 1000 // 1秒转一圈
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        rotationAnimator.start()

        // 1.5秒后隐藏in_clean页面并执行清理
        binding.inClean.inClean.postDelayed({
            binding.inClean.inClean.visibility = View.GONE
            rotationAnimator.cancel()
            
            Thread {
                var deletedCount = 0
                var deletedSize = 0L

                selectedFiles.forEach { trashFile ->
                    try {
                        val file = File(trashFile.path)
                        if (!file.exists() || file.delete()) {
                            deletedCount++
                            deletedSize += trashFile.size
                        }
                    } catch (e: Exception) {
                        deletedCount++
                        deletedSize += trashFile.size
                    }
                    Thread.sleep(50)
                }

                runOnUiThread {
                    val intent = Intent(this, PengSingle::class.java).apply {
                        putExtra("deleted_count", deletedCount)
                        putExtra("deleted_size", deletedSize)
                    }
                    startActivity(intent)
                    finish()
                }
            }.start()
        }, 1500) // 1.5秒延迟
    }
}

data class TrashCategory(
    val name: String,
    val iconRes: Int,
    val files: MutableList<TrashFile>,
    val type: TrashType,
    var isExpanded: Boolean = false,
    var isSelected: Boolean = false,
    var totalSize: Long = 0L
)

data class TrashFile(
    val name: String,
    val path: String,
    val size: Long,
    var isSelected: Boolean = false,
    val type: TrashType
)

enum class TrashType {
    APP_CACHE, APK_FILES, LOG_FILES, TEMP_FILES, ADJUNK
}


