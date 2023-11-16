package com.example.geofencing

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geofencing.databinding.SingleUserBinding
import com.example.geofencing.model.User

class UsersAdapter(val context: Context,val list :List<User>) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding= SingleUserBinding.inflate(LayoutInflater.from(context),parent,false)
        return UserViewHolder(binding)
    }

    override fun getItemCount()=list.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val email=list[position].email
        val name=email.substringBefore("@")
       holder.binding.userTextView.text= name
    }

    class UserViewHolder(val binding: SingleUserBinding) : RecyclerView.ViewHolder(binding.root) {
    }


}