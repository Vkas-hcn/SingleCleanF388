package com.smoke.clears.away.single

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smoke.clears.away.single.databinding.ItemFileBinding

class FileScanAdapter(
    private val files: List<TrashFile>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<FileScanAdapter.FileViewHolder>() {

    class FileViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = inflateBinding<ItemFileBinding>(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        with(holder.binding) {
            tvFileName.text = file.name
            tvFileSize.text = file.size.toSimpleSizeString()

            imgFileSelect.setImageResource(if (file.isSelected) R.drawable.icon_selete else R.drawable.ic_disselete)

            val toggle: () -> Unit = {
                file.isSelected = !file.isSelected
                notifyItemChanged(position)
                onSelectionChanged()
            }

            root.setOnClickListener { toggle() }
            imgFileSelect.setOnClickListener { toggle() }
        }
    }

    override fun getItemCount() = files.size
}