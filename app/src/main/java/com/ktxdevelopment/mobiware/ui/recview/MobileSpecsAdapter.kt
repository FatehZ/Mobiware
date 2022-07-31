package com.ktxdevelopment.mobiware.ui.recview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ktxdevelopment.mobiware.databinding.MobileSpecItemBinding
import com.ktxdevelopment.mobiware.models.rest.product.Spec

class MobileSpecsAdapter : ListAdapter<Spec ,MobileSpecsAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MobileSpecItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!(currentList[position].key == "-" && currentList[position].key == "" &&
                    currentList[position].`val`[0] == "-" && currentList[position].`val`[0] == "")) {
            holder.binding.tvKey.text = currentList[position].key
            holder.binding.tvVal.text = currentList[position].`val`[0]
        }
    }

    class ViewHolder(val binding: MobileSpecItemBinding) : RecyclerView.ViewHolder(binding.root)


    object Diff : DiffUtil.ItemCallback<Spec>() {
        override fun areItemsTheSame(oldItem: Spec, newItem: Spec): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: Spec, newItem: Spec): Boolean {
            return oldItem.`val` == newItem.`val`
        }
    }
}