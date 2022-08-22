package com.ktxdevelopment.mobiware.ui.recview

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ktxdevelopment.mobiware.databinding.SelectionItemBinding
import com.ktxdevelopment.mobiware.models.rest.search.Phone

class LatestMobileAdapter(private val onMobileClickListener: OnMobileClickListener) : ListAdapter<Phone, LatestMobileAdapter.ViewHolder>(Diff) {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
          return ViewHolder(SelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
     }

     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
          holder.binding.root.setOnClickListener {
               Log.i("TAG", "onBindViewHolder: Pressed")
               onMobileClickListener.onPosClick(position) }
          Glide.with(holder.itemView.context)
               .load(currentList[position].image)
               .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                         holder.binding.civSelection.visibility = View.GONE
                         return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                         holder.binding.civSelection.visibility = View.VISIBLE
                         return false
                    }
               })
               .fitCenter()
               .into(holder.binding.civSelection)

          holder.binding.tvSelectionTitle.text = (currentList[position].phone_name)
     }



     class ViewHolder(val binding: SelectionItemBinding) : RecyclerView.ViewHolder(binding.root)

     object Diff : DiffUtil.ItemCallback<Phone>() {
          override fun areItemsTheSame(oldItem: Phone, newItem: Phone): Boolean {
               return newItem.phone_name == oldItem.phone_name }
          override fun areContentsTheSame(oldItem: Phone, newItem: Phone): Boolean{
               return newItem.detail == oldItem.detail }
     }

     interface OnMobileClickListener {
          fun onPosClick(pos: Int)
     }

}