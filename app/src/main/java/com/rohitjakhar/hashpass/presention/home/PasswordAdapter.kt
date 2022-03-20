package com.rohitjakhar.hashpass.presention.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitjakhar.hashpass.data.model.PasswordModel
import com.rohitjakhar.hashpass.databinding.ItemPasswordBinding

class PasswordAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<PasswordModel, PasswordAdapter.PasswordVH>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordVH {
        return PasswordVH(
            ItemPasswordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PasswordVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PasswordVH(private val binding: ItemPasswordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PasswordModel) = binding.apply {
            tvTitle.text = data.title
            tvUserName.text = data.userName ?: data.email
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<PasswordModel>() {
            override fun areContentsTheSame(
                oldItem: PasswordModel,
                newItem: PasswordModel
            ): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areItemsTheSame(oldItem: PasswordModel, newItem: PasswordModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
