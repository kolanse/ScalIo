package com.example.scalio.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scalio.data.model.User
import com.example.scalio.databinding.UserRecyclerItemBinding
import com.example.scalio.utils.loadImage
import javax.inject.Inject

class UsersAdapter @Inject constructor() : PagingDataAdapter<User, UsersAdapter.ItemViewHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User) =
            oldItem == newItem
    }

    inner class ItemViewHolder constructor(val binding: UserRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: User) {
            binding.userType.text = item.type
            binding.userLogin.text = item.login
            binding.userAvatar.loadImage(item.avatarUrl)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = UserRecyclerItemBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = getItem(position)
        item?.let { holder.bind(it) }
    }
}
