package com.smoke.clears.away.single

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smoke.clears.away.single.databinding.ItemFileBinding

class FileScanAdapter(
    private val files: List<TrashFile>,
    private val onToggleSelection: (TrashFile) -> Unit
) : RecyclerView.Adapter<FileScanAdapter.FileViewHolder>() {

    class FileViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        with(holder.binding) {
            tvFileName.text = file.name
            tvFileSize.text = formatFileSize(file.size)

            imgFileSelect.setImageResource(
                if (file.isSelected) R.drawable.icon_selete else R.drawable.ic_disselete
            )

            val toggle: () -> Unit = {
                file.isSelected = !file.isSelected
                imgFileSelect.setImageResource(
                    if (file.isSelected) R.drawable.icon_selete else R.drawable.ic_disselete
                )
                onToggleSelection(file)
            }

            root.setOnClickListener { toggle() }
        }
    }

    override fun getItemCount() = files.size
    
    private fun formatFileSize(size: Long): String {
        return when {
            size >= 1000 * 1000 * 1000 -> String.format("%.2fGB", size / (1000.0 * 1000.0 * 1000.0))
            size >= 1000 * 1000 -> String.format("%.2fMB", size / (1000.0 * 1000.0))
            else -> String.format("%.2fKB", size / 1000.0)
        }
    }
}