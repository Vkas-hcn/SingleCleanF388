package com.smoke.clears.away.single

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.text.DecimalFormat

/**
 * Generic view binding inflate helper using inline + reified to reduce boilerplate.
 * Usage: val binding = parent.inflateBinding<ItemFileBinding>(inflater, parent, false)
 */
inline fun <reified T : ViewBinding> inflateBinding(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    attachToParent: Boolean = false
): T {
    // Reflectively call the generated inflate method: T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    val method = T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    @Suppress("UNCHECKED_CAST")
    return method.invoke(null, inflater, parent, attachToParent) as T
}

// File size formatting extension
fun Long.toHumanReadable(): Pair<String, String> {
    return when {
        this >= 1000L * 1000L * 1000L -> {
            val gb = this.toDouble() / (1000.0 * 1000.0 * 1000.0)
            val formatted = if (gb >= 10.0) DecimalFormat("#").format(gb) else DecimalFormat("#.#").format(gb)
            Pair(formatted, "GB")
        }
        this >= 1000L * 1000L -> {
            val mb = this.toDouble() / (1000.0 * 1000.0)
            val formatted = if (mb >= 10.0) DecimalFormat("#").format(mb) else DecimalFormat("#.#").format(mb)
            Pair(formatted, "MB")
        }
        else -> {
            val kb = this.toDouble() / 1000.0
            Pair(String.format("%.1f", kb), "KB")
        }
    }
}

fun Long.toSimpleSizeString(): String {
    val (value, unit) = this.toHumanReadable()
    return "$value $unit"
}


