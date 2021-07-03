package com.shivam.letstalk.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.shivam.letstalk.R
import com.shivam.letstalk.listeners.UsersListener
import com.shivam.letstalk.models.User

class UserAdapter(private val usersList: ArrayList<User>, val usersListener: UsersListener) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textFirstChar = itemView.findViewById<TextView>(R.id.textFirstChar)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvIncomingUserName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val ivAudioMeeting: ImageView = itemView.findViewById(R.id.ivAudioMeeting)
        val ivVideoMeeting: ImageView = itemView.findViewById(R.id.ivVideoMeeting)

        fun bind(user:User){
            textFirstChar.text = user.firstName?.substring(0,1)
            tvUserName.text = String.format("%s %s", user.firstName, user.lastName)
            tvEmail.text = user.email

            ivAudioMeeting.setOnClickListener{
                usersListener.initiateAudioMeeting(user)
            }
            ivVideoMeeting.setOnClickListener{
                usersListener.initiateVideoMeeting(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(usersList[position])
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    fun updateList(newsList: List<User>){
        usersList.addAll(newsList)
        notifyDataSetChanged()
    }

}