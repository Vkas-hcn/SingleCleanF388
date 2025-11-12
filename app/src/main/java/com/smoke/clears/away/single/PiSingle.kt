package com.smoke.clears.away.single

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.smoke.clears.away.single.databinding.SinglePiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class PiSingle : AppCompatActivity() {
    private lateinit var binding: SinglePiBinding
    private val trashCategories = mutableListOf<TrashCategory>()
    private var totalTrashSize = 0L
    private val isScanning = AtomicBoolean(false)
    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(trashCategories) { updateCleanButtonState() }
    }
    
    private val fileProcessors = listOf<FileProcessor>(
        AppCacheProcessor(),
        ApkProcessor(),
        LogProcessor(),
        TempProcessor(),
        AdJunkProcessor()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SinglePiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        setupViews()
        startScanning()
    }
    
    private fun setupViews() {
        binding.btnBack.setOnClickListener { finish() }
        
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this@PiSingle)
            adapter = categoryAdapter
        }
        
        binding.btnCleanNow.setOnClickListener { cleanSelectedFiles() }
        binding.btnCleanNow.visibility = View.GONE
        
        binding.inClean.inClean.setOnClickListener {}
        binding.inClean.imgBack.setOnClickListener { finish() }
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
                launch(Dispatchers.Main) {
                    binding.tvScanningPath.text = "Scanning: $path"
                }
            }
            finishScanning()
            isScanning.set(false)
        }
    }
    
    private fun initializeCategories() {
        trashCategories.clear()
        
        // 初始化所有垃圾分类，即使没有数据也显示
        val allTrashItems = listOf(
            TrashItem.AppCache,
            TrashItem.ApkFiles,
            TrashItem.LogFiles,
            TrashItem.TempFiles,
            TrashItem.AdJunk
        )
        
        allTrashItems.forEach { trashItem ->
            val category = TrashCategory(trashItem)
            trashCategories.add(category)
        }
        
        categoryAdapter.notifyDataSetChanged()
    }
    
    private suspend fun scanForTrashFilesSuspend(onPath: (String) -> Unit) {
        val rootDir = Environment.getExternalStorageDirectory()
        val allFiles = mutableListOf<File>()
        
        try {
            rootDir.walk().forEach { file ->
                if (file.isFile) {
                    allFiles.add(file)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        val totalFiles = allFiles.size
        var processedFiles = 0
        
        allFiles.forEach { file ->
            if (!isScanning.get()) return
            
            onPath(file.absolutePath)
            processFileWithProcessors(file)
            
            processedFiles++
            val progress = (processedFiles * 100) / totalFiles.coerceAtLeast(1)
            
            // 在协程中更新UI
            lifecycleScope.launch(Dispatchers.Main) {
                binding.progressScaning.progress = progress
                updateTrashSize(totalTrashSize)
            }
            
            delay(10)
        }
    }
    
    private fun processFileWithProcessors(file: File) {
        fileProcessors.forEach { processor ->
            val processedFile = processor.processFile(file)
            if (processedFile != null) {
                addTrashFile(processor.getFileType(), processedFile)
                return
            }
        }
    }
    
    private fun addTrashFile(item: TrashItem, fileData: Map<String, Any>) {
        val category = trashCategories.find { it.type == item.type }
        if (category != null) {
            category.addFile(fileData.toMutableMap())
        } else {
            val newCategory = TrashCategory(item).apply {
                addFile(fileData.toMutableMap())
            }
            trashCategories.add(newCategory)
        }
        totalTrashSize += fileData["size"] as? Long ?: 0L
    }
    
    private fun finishScanning() {
        binding.progressScaning.visibility = View.GONE
        
        // 扫描完成后切换背景
        binding.root.setBackgroundResource(R.drawable.bg_lj)
        
        if (trashCategories.isNotEmpty()) {
            binding.btnCleanNow.visibility = View.VISIBLE
            trashCategories.forEach { category ->
                category.selectAll(true)
            }
            updateCleanButtonState()
        } else {
            binding.tvScanningPath.text = "No trash files found"
        }
        
        categoryAdapter.notifyDataSetChanged()
    }
    
    private fun updateTrashSize(size: Long) {
        val sizeText = when {
            size >= 1000 * 1000 * 1000 -> "%.2f GB".format(size / (1000.0 * 1000.0 * 1000.0))
            size >= 1000 * 1000 -> "%.2f MB".format(size / (1000.0 * 1000.0))
            else -> "%.2f KB".format(size / 1000.0)
        }
        binding.tvScannedSize.text = sizeText.split(" ")[0]
        binding.tvScannedSizeUn.text = sizeText.split(" ")[1]
    }
    
    private fun updateCleanButtonState() {
        val hasSelectedFiles = trashCategories.any { category ->
            category.getSelectedFiles().isNotEmpty()
        }
        binding.btnCleanNow.isEnabled = hasSelectedFiles
        binding.btnCleanNow.alpha = if (hasSelectedFiles) 1f else 0.5f
    }
    
    private fun cleanSelectedFiles() {
        val filesToDelete = mutableListOf<File>()
        
        trashCategories.forEach { category ->
            category.getSelectedFiles().forEach { fileData ->
                val path = fileData["path"] as? String ?: return@forEach
                filesToDelete.add(File(path))
            }
        }
        
        if (filesToDelete.isEmpty()) {
            Toast.makeText(this, "Please select files to clean", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 显示清理动画
        binding.inClean.inClean.visibility = View.VISIBLE
        val imgLoad = binding.inClean.inClean.findViewById<ImageView>(R.id.img_load)
        val rotationAnimator = ObjectAnimator.ofFloat(imgLoad, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        rotationAnimator.start()
        
        // 执行清理
        lifecycleScope.launch {
            var deletedCount = 0
            var deletedSize = 0L
            
            filesToDelete.forEach { file ->
                try {
                    val size = file.length()
                    if (file.delete()) {
                        deletedCount++
                        deletedSize += size
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(20)
            }
            
            // 更新数据
            trashCategories.forEach { category ->
                val selectedFiles = category.getSelectedFiles()
                selectedFiles.forEach { fileData ->
                    category.files.toMutableList().remove(fileData)
                }
            }
            trashCategories.removeAll { it.files.isEmpty() }
            
            withContext(Dispatchers.Main) {
                rotationAnimator.cancel()
                binding.inClean.inClean.visibility = View.GONE
                
                val message = "Deleted $deletedCount files (${formatSize(deletedSize)})"
                Toast.makeText(this@PiSingle, message, Toast.LENGTH_SHORT).show()
                
                categoryAdapter.notifyDataSetChanged()
                updateCleanButtonState()
                
                if (trashCategories.isEmpty()) {
                    binding.tvScanningPath.text = "No trash files remaining"
                }

                // 跳转到结果页，传入删除数量与大小
                val intent = Intent(this@PiSingle, PengSingle::class.java).apply {
                    putExtra("deleted_count", deletedCount)
                    putExtra("deleted_size", deletedSize)
                }
                startActivity(intent)
                finish()
            }
        }
    }
    
    private fun formatSize(size: Long): String {
        return when {
            size >= 1000 * 1000 * 1000 -> "%.2f GB".format(size / (1000.0 * 1000.0 * 1000.0))
            size >= 1000 * 1000 -> "%.2f MB".format(size / (1000.0 * 1000.0))
            else -> "%.2f KB".format(size / 1000.0)
        }
    }
}

// 使用密封类替代枚举，提供更灵活的类型系统
sealed class TrashItem {
    abstract val name: String
    abstract val iconRes: Int
    abstract val type: String
    
    object AppCache : TrashItem() {
        override val name = "App Cache"
        override val iconRes = R.drawable.icon_cache
        override val type = "app_cache"
    }
    
    object ApkFiles : TrashItem() {
        override val name = "APK Files"
        override val iconRes = R.drawable.icon_apk
        override val type = "apk_files"
    }
    
    object LogFiles : TrashItem() {
        override val name = "Log Files"
        override val iconRes = R.drawable.icon_log_files
        override val type = "log_files"
    }
    
    object TempFiles : TrashItem() {
        override val name = "Temp Files"
        override val iconRes = R.drawable.icon_temp_files
        override val type = "temp_files"
    }
    
    object AdJunk : TrashItem() {
        override val name = "Ad Junk"
        override val iconRes = R.drawable.icon_ad_junk
        override val type = "ad_junk"
    }
}

// 使用Map结构替代数据类，提供更动态的数据结构
class TrashCategory(
    val item: TrashItem,
    private val _files: MutableList<MutableMap<String, Any>> = mutableListOf()
) {
    var isExpanded: Boolean = false
    var isSelected: Boolean = false
    
    val name: String get() = item.name
    val iconRes: Int get() = item.iconRes
    val type: String get() = item.type
    
    val files: List<MutableMap<String, Any>> get() = _files.toList()
    val totalSize: Long get() = _files.sumOf { it["size"] as? Long ?: 0L }
    
    fun addFile(file: MutableMap<String, Any>) {
        _files.add(file)
    }
    
    fun clearFiles() {
        _files.clear()
    }
    
    fun getSelectedFiles(): List<MutableMap<String, Any>> = 
        _files.filter { it["isSelected"] as? Boolean ?: false }
    
    fun selectAll(selected: Boolean) {
        _files.forEach { it["isSelected"] = selected }
    }
    
    fun allFilesSelected(): Boolean = 
        _files.isNotEmpty() && _files.all { it["isSelected"] as? Boolean ?: false }

    fun setFileSelectedByPath(path: String, selected: Boolean) {
        _files.firstOrNull { it["path"] == path }?.let { it["isSelected"] = selected }
    }
}

// 使用接口抽象化文件操作
interface FileProcessor {
    fun processFile(file: File): Map<String, Any>?
    fun getFileType(): TrashItem
}

class AppCacheProcessor : FileProcessor {
    override fun processFile(file: File): Map<String, Any>? {
        return if (file.isDirectory && file.name == "cache") {
            mapOf(
                "name" to file.name,
                "path" to file.absolutePath,
                "size" to calculateDirSize(file),
                "isSelected" to false,
                "type" to TrashItem.AppCache.type
            )
        } else null
    }
    
    override fun getFileType() = TrashItem.AppCache
    
    private fun calculateDirSize(dir: File): Long {
        var size = 0L
        try {
            dir.walk().forEach { file ->
                if (file.isFile) {
                    size += file.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }
}

class ApkProcessor : FileProcessor {
    override fun processFile(file: File): Map<String, Any>? {
        return if (file.extension.equals("apk", ignoreCase = true)) {
            mapOf(
                "name" to file.name,
                "path" to file.absolutePath,
                "size" to file.length(),
                "isSelected" to false,
                "type" to TrashItem.ApkFiles.type
            )
        } else null
    }
    
    override fun getFileType() = TrashItem.ApkFiles
}

class LogProcessor : FileProcessor {
    override fun processFile(file: File): Map<String, Any>? {
        return if (file.extension.equals("log", ignoreCase = true)) {
            mapOf(
                "name" to file.name,
                "path" to file.absolutePath,
                "size" to file.length(),
                "isSelected" to false,
                "type" to TrashItem.LogFiles.type
            )
        } else null
    }
    
    override fun getFileType() = TrashItem.LogFiles
}

class TempProcessor : FileProcessor {
    override fun processFile(file: File): Map<String, Any>? {
        return if (file.extension in listOf("tmp", "temp")) {
            mapOf(
                "name" to file.name,
                "path" to file.absolutePath,
                "size" to file.length(),
                "isSelected" to false,
                "type" to TrashItem.TempFiles.type
            )
        } else null
    }
    
    override fun getFileType() = TrashItem.TempFiles
}

class AdJunkProcessor : FileProcessor {
    override fun processFile(file: File): Map<String, Any>? {
        val adPatterns = listOf("pang", "advert", "popup", "banner")
        return if (adPatterns.any { pattern -> file.name.contains(pattern, ignoreCase = true) }) {
            mapOf(
                "name" to file.name,
                "path" to file.absolutePath,
                "size" to file.length(),
                "isSelected" to false,
                "type" to TrashItem.AdJunk.type
            )
        } else null
    }
    
    override fun getFileType() = TrashItem.AdJunk
}

// 为了保持与现有适配器的兼容性，保留原始数据类
data class TrashFile(
    val name: String,
    val path: String,
    val size: Long,
    var isSelected: Boolean = false,
    val type: TrashType
)

enum class TrashType {
    APP_CACHE,
    APK_FILES,
    LOG_FILES,
    TEMP_FILES,
    ADJUNK
}


