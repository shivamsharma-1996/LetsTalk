package com.shivam.letstalk.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shivam.letstalk.R
import com.shivam.letstalk.models.User
import kotlinx.android.synthetic.main.activity_incoming_invitation.ivMeetingType
import kotlinx.android.synthetic.main.activity_incoming_invitation.tvFirstChar
import kotlinx.android.synthetic.main.activity_outgoing_invitation.*

class OutgoingInvitationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            R.layout.activity_outgoing_invitation)

        val meetingType = intent.getStringExtra("type")
        meetingType?.let {
            when(meetingType){
                "video" -> ivMeetingType.setImageResource(R.drawable.ic_video)
            }
        }

        val user: User? = intent.getSerializableExtra("user") as User?
        if(user != null){
            tvFirstChar.text = user.firstName!!.substring(0,1)
            tvOutgoingUserName.text = String.format("%s %s", user.firstName, user.lastName)
            tvOutgoingUserEmail.text = user.email
        }
        ivStopInvitation.setOnClickListener{
            onBackPressed()
        }
    }
}
