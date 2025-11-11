package com.smoke.clears.away.single

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.smoke.clears.away.single.databinding.SingleTanBinding
import com.smoke.clears.away.single.databinding.ViewCpuInfoRowBinding
import java.io.File
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class TanSingle : AppCompatActivity() {
    private val binding by lazy { SingleTanBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scroll_content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.inScan.inClean.setOnClickListener {
        }
        binding.inScan.imgBack.setOnClickListener {
            finish()
        }

        showPreScanThen {
            populateCpuInfo()
        }
    }

    private fun showPreScanThen(after: () -> Unit) {
        val overlay = findViewById<View>(R.id.in_scan)
        val tvTip = overlay.findViewById<TextView>(R.id.tv_tip)
        val imgLogo = overlay.findViewById<ImageView>(R.id.img_load)
        tvTip.text = "Scanning"
        val animator = ObjectAnimator.ofFloat(imgLogo, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        overlay.visibility = View.VISIBLE
        animator.start()
        overlay.postDelayed({
            animator.cancel()
            overlay.visibility = View.GONE
            after()
        }, 1500)
    }

    private fun populateCpuInfo() {
        val info = CpuInfoCollector().collect()

        binding.tvCpuName.text = info.displayName.orDash()

        setRow(binding.rowCpu, "CPU", info.cpuCode)
        setRow(binding.rowVendor, "Vendor", info.vendor)
        setRow(binding.rowCores, "Cores", info.cores)
        setRow(binding.rowBigLittle, "big.LITTLE", info.bigLittle)
        setRow(binding.rowClusters, "Clusters", info.clusters)
        setRow(binding.rowFamily, "Family", info.family)
        setRow(binding.rowMode, "Mode", info.mode)
        setRow(binding.rowMachine, "Machine", info.machine)
        setRow(binding.rowAbi, "ABI", info.abi)
        setRow(binding.rowInstructions, "Instructions", info.instructions)
        setRow(binding.rowProcessTech, "Process Technology", info.processTechnology)
        setRow(binding.rowManufacturing, "Manufacturing", info.manufacturing)
        setRow(binding.rowRevision, "Revision", info.revision)
        setRow(binding.rowClockSpeed, "Clock Speed", info.clockSpeed)
        setRow(binding.rowGovernor, "Governor", info.governor)
        setRow(binding.rowSupportedAbi, "Supported ABI", info.supportedAbi)
        setRow(binding.rowGpu, "GPU", info.gpu)
    }

    private fun setRow(row: ViewCpuInfoRowBinding, label: String, value: String?) {
        row.tvLabel.text = label
        row.tvValue.text = value.orDash()
    }

    private fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this

    private class CpuInfoCollector {
        fun collect(): CpuInfo {
            val cpuInfoMap = readCpuInfoMap()
            val locale = Locale.getDefault()

            val socModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Build.SOC_MODEL
            } else {
                null
            }

            val hardwareRaw = cpuInfoMap["Hardware"] ?: cpuInfoMap["model name"]
            val displayName = when {
                !socModel.isNullOrBlank() -> socModel.trim()
                !hardwareRaw.isNullOrBlank() -> hardwareRaw.trim()
                !Build.HARDWARE.isNullOrBlank() -> Build.HARDWARE
                !Build.MODEL.isNullOrBlank() -> Build.MODEL
                else -> null
            }

            val cpuCode = hardwareRaw
                ?.substringAfterLast(" ")
                ?.takeIf { it.isNotBlank() }
                ?.lowercase(locale)
                ?: hardwareRaw?.trim()

            val vendor = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !Build.SOC_MANUFACTURER.isNullOrBlank() -> {
                    Build.SOC_MANUFACTURER
                }
                !cpuInfoMap["Vendor ID"].isNullOrBlank() -> cpuInfoMap["Vendor ID"]
                !cpuInfoMap["vendor_id"].isNullOrBlank() -> cpuInfoMap["vendor_id"]
                else -> Build.MANUFACTURER
            }?.lowercase(locale)

            val clusterCount = getClusterCount()

            val abiList = when {
                Build.SUPPORTED_64_BIT_ABIS.isNotEmpty() -> Build.SUPPORTED_64_BIT_ABIS.toList()
                Build.SUPPORTED_32_BIT_ABIS.isNotEmpty() -> Build.SUPPORTED_32_BIT_ABIS.toList()
                else -> Build.SUPPORTED_ABIS.toList()
            }

            val instructions = cpuInfoMap["Features"] ?: cpuInfoMap["flags"]

            val processTechnology = getSystemProperty("ro.soc.manufacturing_process")
                ?: getSystemProperty("ro.soc.process")

            val manufacturing = getSystemProperty("ro.soc.manufacturer")
                ?: vendor

            val revision = cpuInfoMap["Revision"]

            val clockSpeed = getClockSpeed()
            val governor = getGovernor()

            val supportedAbi = Build.SUPPORTED_ABIS.joinToString(", ")

            val gpu = getGpuInfo()

            return CpuInfo(
                displayName = displayName,
                cpuCode = cpuCode,
                vendor = vendor,
                cores = Runtime.getRuntime().availableProcessors().toString(),
                bigLittle = if (clusterCount > 1) vendor else null,
                clusters = if (clusterCount > 0) clusterCount.toString() else "-",
                family = vendor,
                mode = if (Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()) "64-bit" else "32-bit",
                machine = System.getProperty("os.arch"),
                abi = abiList.joinToString(", ").ifBlank { null },
                instructions = instructions,
                processTechnology = processTechnology,
                manufacturing = manufacturing,
                revision = revision,
                clockSpeed = clockSpeed,
                governor = governor,
                supportedAbi = if (supportedAbi.isBlank()) null else supportedAbi,
                gpu = gpu
            )
        }

        private fun readCpuInfoMap(): Map<String, String> {
            val map = mutableMapOf<String, String>()
            runCatching {
                File("/proc/cpuinfo").forEachLine { line ->
                    val parts = line.split(":", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parts[1].trim()
                        if (key.isNotEmpty() && value.isNotEmpty()) {
                            map[key] = value
                        }
                    }
                }
            }
            return map
        }

        private fun getClusterCount(): Int {
            val cpuCount = Runtime.getRuntime().availableProcessors()
            val clusters = mutableSetOf<String>()
            for (i in 0 until cpuCount) {
                val path = "/sys/devices/system/cpu/cpu$i/topology/cluster_id"
                val value = readFirstLine(path)
                if (!value.isNullOrBlank()) {
                    clusters.add(value)
                }
            }
            return clusters.size
        }

        private fun getClockSpeed(): String? {
            val cpuCount = Runtime.getRuntime().availableProcessors()
            var minFreqHz: Long? = null
            var maxFreqHz: Long? = null
            for (i in 0 until cpuCount) {
                val minPath = "/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_min_freq"
                val maxPath = "/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_max_freq"
                val minFreq = readFirstLine(minPath)?.toLongOrNull()
                val maxFreq = readFirstLine(maxPath)?.toLongOrNull()
                if (minFreq != null) {
                    minFreqHz = minFreqHz?.let { min(it, minFreq) } ?: minFreq
                }
                if (maxFreq != null) {
                    maxFreqHz = maxFreqHz?.let { max(it, maxFreq) } ?: maxFreq
                }
            }

            if (minFreqHz == null && maxFreqHz == null) return null

            val minMhz = minFreqHz?.div(1000)
            val maxMhz = maxFreqHz?.div(1000)

            return when {
                minMhz != null && maxMhz != null -> "${minMhz}-${maxMhz} MHz"
                maxMhz != null -> "${maxMhz} MHz"
                minMhz != null -> "${minMhz} MHz"
                else -> null
            }
        }

        private fun getGovernor(): String? {
            val governor = readFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor")
            return governor?.ifBlank { null }
        }

        private fun getGpuInfo(): String? {
            val kgslModel = readFirstLine("/sys/class/kgsl/kgsl-3d0/gpu_model")
            if (!kgslModel.isNullOrBlank()) {
                return kgslModel.trim()
            }
            val kgslVendor = readFirstLine("/sys/class/kgsl/kgsl-3d0/vendor")
            if (!kgslVendor.isNullOrBlank()) {
                return kgslVendor.trim()
            }

            return getSystemProperty("ro.gpu.name")
                ?: getSystemProperty("ro.hardware.egl")
                ?: getSystemProperty("ro.board.platform")
        }

        private fun readFirstLine(path: String): String? = runCatching {
            val file = File(path)
            if (file.exists()) {
                file.bufferedReader().use { it.readLine()?.trim() }
            } else null
        }.getOrNull()

        private fun getSystemProperty(property: String): String? {
            return runCatching {
                val clazz = Class.forName("android.os.SystemProperties")
                val method = clazz.getMethod("get", String::class.java, String::class.java)
                val value = method.invoke(null, property, "") as String
                value.takeIf { it.isNotBlank() }
            }.getOrNull()
        }
    }

    private data class CpuInfo(
        val displayName: String?,
        val cpuCode: String?,
        val vendor: String?,
        val cores: String?,
        val bigLittle: String?,
        val clusters: String?,
        val family: String?,
        val mode: String?,
        val machine: String?,
        val abi: String?,
        val instructions: String?,
        val processTechnology: String?,
        val manufacturing: String?,
        val revision: String?,
        val clockSpeed: String?,
        val governor: String?,
        val supportedAbi: String?,
        val gpu: String?
    )
}