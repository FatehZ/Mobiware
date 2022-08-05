package com.ktxdevelopment.mobiware.ui.recview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ktxdevelopment.mobiware.databinding.SelectionItemBinding
import com.ktxdevelopment.mobiware.models.rest.search.Phone

class TopMobileAdapter(private val onMobileClickListener: OnTopMobileClickListener) : ListAdapter<Phone, TopMobileAdapter.ViewHolder>(Diff) {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
          return ViewHolder(SelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
     }

     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
          Glide.with(holder.itemView.context)
               .load(currentList[position].image)
               .fitCenter()
               .into(holder.binding.civSelection)

          holder.binding.tvSelectionTitle.text = (currentList[position].phone_name)
          holder.binding.root.setOnClickListener { onMobileClickListener.onPosClick(position) }
     }



     class ViewHolder(val binding: SelectionItemBinding) : RecyclerView.ViewHolder(binding.root)

     object Diff : DiffUtil.ItemCallback<Phone>() {
          override fun areItemsTheSame(oldItem: Phone, newItem: Phone): Boolean { return newItem.phone_name == oldItem.phone_name }
          override fun areContentsTheSame(oldItem: Phone, newItem: Phone): Boolean{ return newItem.detail == oldItem.detail }
     }

     interface OnTopMobileClickListener {
          fun onPosClick(pos: Int)
     }

}