package com.example.newsapp.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.databinding.ViewUserItemBinding
import com.example.newsapp.domain.model.User
import com.example.newsapp.ui.users.UsersAdapter.UserViewHolder
import javax.inject.Inject

class UsersAdapter(
    private val listener: UsersAdapterListener,
    private var list: MutableList<User> = mutableListOf()
) : RecyclerView.Adapter<UserViewHolder>() {

    interface UsersAdapterListener {
        fun onGoToMapClicked(userId: String, userName: String)
    }

    inner class UserViewHolder(private val binding: ViewUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                with(user) {
                    usernameTv.text = userName
                    companyTv.text = companyName
                    goToMapTv.setOnClickListener {
                        listener.onGoToMapClicked(id, userName)
                    }
                }
            }
        }
    }

    fun setNewList(newList: List<User>) {
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areItemsTheSame(list[oldItemPosition], newList[newItemPosition])

            override fun getOldListSize() = list.size

            override fun getNewListSize() = newList.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                areContentsTheSame(list[oldItemPosition], newList[newItemPosition])
        })

        list = newList.toMutableList()
        result.dispatchUpdatesTo(this)
    }

    private fun areItemsTheSame(oldUser: User, user: User): Boolean {
        return oldUser.id == user.id
    }

    private fun areContentsTheSame(oldUser: User, user: User): Boolean {
        return oldUser == user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ViewUserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
