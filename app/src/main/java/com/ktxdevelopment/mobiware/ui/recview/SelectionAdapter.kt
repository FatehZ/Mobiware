package com.ktxdevelopment.mobiware.ui.recview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ktxdevelopment.mobiware.databinding.SelectionItemBinding
import com.ktxdevelopment.mobiware.models.rest.search.Phone

class SelectionAdapter(private var onMobileClickListener: OnMobileClickListener) : RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder>() {

    var list: List<Phone> = ArrayList()

    class SelectionViewHolder(val binding: SelectionItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        return SelectionViewHolder(SelectionItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(list[position].image)
            .fitCenter()
            .into(holder.binding.civSelection)

        holder.binding.tvSelectionTitle.text = (list[position].brand + " " + list[position].phone_name)
        holder.binding.root.setOnClickListener { onMobileClickListener.onMobileClick(position) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(phones: List<Phone>) {
        list = phones
        notifyDataSetChanged()
    }

    interface OnMobileClickListener{
        fun onMobileClick(position: Int)
    }
}