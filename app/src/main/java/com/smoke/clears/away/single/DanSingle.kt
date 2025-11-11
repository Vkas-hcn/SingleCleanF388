package com.smoke.clears.away.single

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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
import com.smoke.clears.away.single.databinding.SingleDanBinding
import com.smoke.clears.away.single.databinding.ViewCpuInfoRowBinding
import java.io.File
import kotlin.math.abs

class DanSingle : AppCompatActivity() {
    private val binding by lazy { SingleDanBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dan)) { v, insets ->
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
            populateBatteryInfo()
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

    private fun populateBatteryInfo() {
        val info = BatteryInfoCollector(this).collect()

        // Header title is static "Battery", we only fill rows
        setRow(binding.rowLevel, "LEVEL", info.level)
        setRow(binding.rowHealth, "Health", info.health)
        setRow(binding.rowStatus, "Status", info.status)
        setRow(binding.rowSource, "Power source", info.powerSource)
        setRow(binding.rowTech, "Technology", info.technology)
        setRow(binding.rowTemp, "Temperature", info.temperature)
        setRow(binding.rowVoltage, "Voltage", info.voltage)
        setRow(binding.rowDischarge, "Discharge Speed", info.dischargeSpeed)
        setRow(binding.rowProfile, "Power Profile", info.powerProfile)
    }

    private fun setRow(row: ViewCpuInfoRowBinding, label: String, value: String?) {
        row.tvLabel.text = label
        row.tvValue.text = value?.takeIf { it.isNotBlank() } ?: "-"
    }
}

private class BatteryInfoCollector(private val activity: AppCompatActivity) {
    fun collect(): BatteryInfo {
        val bm = activity.getSystemService(BatteryManager::class.java)
        val intent = activity.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val levelPct = intent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) "${(level * 100 / scale)}%" else null
        }

        val health = when (intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> null
        }

        val status = when (intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
            else -> null
        }

        val plugged = when (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Battery"
        }

        val tech = intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

        val temp = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        val tempStr = temp?.takeIf { it != Int.MIN_VALUE }?.let { String.format("%.1f°c", it / 10f) }

        val voltageMv = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, Int.MIN_VALUE)
        val voltageStr = voltageMv?.takeIf { it != Int.MIN_VALUE }?.let { "${it} mV" }

        val currentUa = getCurrentNowMicroAmps(bm)
        val dischargeStr = currentUa?.let {
            val ma = abs(it) / 1000.0
            String.format("%.1f mA", ma)
        }

        val powerProfile = getBatteryCapacityMah()?.let { String.format("%.0f mAh", it) }

        return BatteryInfo(
            level = levelPct,
            health = health,
            status = status,
            powerSource = plugged,
            technology = tech,
            temperature = tempStr,
            voltage = voltageStr,
            dischargeSpeed = dischargeStr,
            powerProfile = powerProfile
        )
    }

    private fun getCurrentNowMicroAmps(bm: BatteryManager?): Int? {
        // Prefer BatteryManager property
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val v = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            if (v != null && v != Int.MIN_VALUE && v != 0) return v
        }
        // Fallback to sysfs
        val candidates = listOf(
            "/sys/class/power_supply/battery/current_now",
            "/sys/class/power_supply/Battery/current_now"
        )
        for (path in candidates) {
            val s = runCatching { File(path).takeIf { it.exists() }?.readText()?.trim() }.getOrNull()
            val value = s?.toIntOrNull()
            if (value != null) return value
        }
        return null
    }

    private fun getBatteryCapacityMah(): Double? {
        // Some devices expose capacity via property; often not available
        val props = listOf("ro.boot.batt_capacity", "persist.sys.batt.capacity")
        props.forEach { key ->
            val v = getSystemProperty(key)
            v?.toDoubleOrNull()?.let { return it }
        }
        // Try charge counter and capacity percentage to estimate (not always available)
        return null
    }

    private fun getSystemProperty(property: String): String? {
        return runCatching {
            val c = Class.forName("android.os.SystemProperties")
            val m = c.getMethod("get", String::class.java, String::class.java)
            val value = m.invoke(null, property, "") as String
            value.takeIf { it.isNotBlank() }
        }.getOrNull()
    }
}

private data class BatteryInfo(
    val level: String?,
    val health: String?,
    val status: String?,
    val powerSource: String?,
    val technology: String?,
    val temperature: String?,
    val voltage: String?,
    val dischargeSpeed: String?,
    val powerProfile: String?
)