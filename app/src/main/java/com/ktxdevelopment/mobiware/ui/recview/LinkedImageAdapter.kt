package com.ktxdevelopment.mobiware.ui.recview

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ktxdevelopment.mobiware.clients.main.PermissionClient
import com.ktxdevelopment.mobiware.databinding.LinkedImageItemBinding

class LinkedImageAdapter(private val onLinkedImageClickListener: OnLinkedImageClickListener) : ListAdapter<Uri, LinkedImageAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LinkedImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    class ViewHolder(val binding: LinkedImageItemBinding) : RecyclerView.ViewHolder(binding.root)


    object Diff : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return true
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvLinkedImage.text = PermissionClient.getImageFileName(holder.itemView.context.contentResolver ,currentList[position])
        holder.binding.tvLinkedImage.text = PermissionClient.getImageFileName(holder.itemView.context.contentResolver ,currentList[position])
        
        holder.binding.btnCancelImage.setOnClickListener {
            onLinkedImageClickListener.onLinkedClick(position)
        }
    }

    interface OnLinkedImageClickListener {
        fun onLinkedClick(position: Int)
    }
}