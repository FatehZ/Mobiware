package com.ktxdevelopment.mobiware.ui.recview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ktxdevelopment.mobiware.clients.firebase.ListUtilClient
import com.ktxdevelopment.mobiware.databinding.ItemBrandListBinding
import com.ktxdevelopment.mobiware.models.main.BrandModel


class BrandListAdapter(private val onBrandClickListener: OnBrandClickListener) : RecyclerView.Adapter<BrandListAdapter.ViewHolder>() {
     private lateinit var list: ArrayList<BrandModel>

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
          return ViewHolder(ItemBrandListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
     }

     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
          Glide.with(holder.itemView.context)
               .load(ListUtilClient.getDeviceModelLogo(list[position].brand_name))
               .fitCenter()
               .into(holder.binding.ivBrandImage)
          holder.binding.root.setOnClickListener { onBrandClickListener.onBrandClick(list[position]) }
     }



     class ViewHolder(val binding: ItemBrandListBinding) : RecyclerView.ViewHolder(binding.root)


     interface OnBrandClickListener {
          fun onBrandClick(brand: BrandModel)
     }

     override fun getItemCount(): Int {
          return list.size
     }

     fun setData(data: ArrayList<BrandModel>) {
          list = data
          notifyDataSetChanged()
     }
}