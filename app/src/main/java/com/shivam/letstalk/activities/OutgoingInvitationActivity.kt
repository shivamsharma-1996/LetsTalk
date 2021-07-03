package com.shivam.letstalk.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessaging
import com.shivam.letstalk.R
import com.shivam.letstalk.models.User
import com.shivam.letstalk.network.RetrofitManager
import com.shivam.letstalk.util.Constants
import com.shivam.letstalk.util.Constants.KEY_EMAIL
import com.shivam.letstalk.util.Constants.KEY_FIRST_NAME
import com.shivam.letstalk.util.Constants.KEY_LAST_NAME
import com.shivam.letstalk.util.Constants.REMOTE_MSG_DATA
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION_ACCEPTED
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION_CANCELLED
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION_REJECTED
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION_RESPONSE
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITOR_TOKEN
import com.shivam.letstalk.util.Constants.REMOTE_MSG_MEETING_TYPE
import com.shivam.letstalk.util.Constants.REMOTE_MSG_REGISTRATION_IDS
import com.shivam.letstalk.util.Constants.REMOTE_MSG_TYPE
import com.shivam.letstalk.util.retrieveString
import kotlinx.android.synthetic.main.activity_incoming_invitation.ivMeetingType
import kotlinx.android.synthetic.main.activity_incoming_invitation.tvFirstChar
import kotlinx.android.synthetic.main.activity_outgoing_invitation.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class OutgoingInvitationActivity : AppCompatActivity() {
    private var invitorToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            R.layout.activity_outgoing_invitation
        )

        invitorToken = intent.getStringExtra(REMOTE_MSG_INVITOR_TOKEN)

        val meetingType = intent.getStringExtra("type")
        meetingType?.let {
            when (meetingType) {
                "video" -> ivMeetingType.setImageResource(R.drawable.ic_video)
            }
        }

        val user: User? = intent.getSerializableExtra("user") as User?
        if (user != null) {
            tvFirstChar.text = user.firstName!!.substring(0, 1)
            tvOutgoingUserName.text = String.format("%s %s", user.firstName, user.lastName)
            tvOutgoinUserEmail.text = user.email
        }
        ivStopInvitation.setOnClickListener {
            if(user?.token != null){
                cancelInvitation(user.token!!)
            }else{
                Toast.makeText(
                    this@OutgoingInvitationActivity,
                    "Something goes wrong!", Toast.LENGTH_LONG
                ).show()
            }
        }

        if(meetingType!=null && user!=null){
            if(user.token==null){
                Toast.makeText(
                    this@OutgoingInvitationActivity,
                    "Can't initiate meeting due to missing token!", Toast.LENGTH_LONG
                ).show()
                return
            }
            initiateMeeting(meetingType, user.token!!)
        }
    }

    private fun initiateMeeting(meetingType:String, receiverToken: String){
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION)
            data.put(REMOTE_MSG_MEETING_TYPE, meetingType)
            data.put(KEY_FIRST_NAME, retrieveString(KEY_FIRST_NAME))
            data.put(KEY_LAST_NAME, retrieveString(KEY_LAST_NAME))
            data.put(KEY_EMAIL, retrieveString(KEY_EMAIL))
            data.put(REMOTE_MSG_INVITOR_TOKEN, invitorToken)

            body.put(REMOTE_MSG_DATA, data)
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(),  REMOTE_MSG_INVITATION)

        }catch (e: Exception){

        }
    }

    private fun sendRemoteMessage(remoteMessageBody: String, type: String){
        RetrofitManager.newsInstance.sendRemoteMessage(
            Constants.getRemoteMessageHeaders(), remoteMessageBody)
            .enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        when(type){
                            REMOTE_MSG_INVITATION -> {
                                Toast.makeText(
                                    this@OutgoingInvitationActivity,
                                    "Invitation sent successfully", Toast.LENGTH_LONG
                                ).show()
                            }

                            REMOTE_MSG_INVITATION_RESPONSE -> {
                                Toast.makeText(
                                    this@OutgoingInvitationActivity,
                                    "Invitation Cancelled", Toast.LENGTH_LONG
                                ).show()
                                finish()
                            }
                        }
                    }else{
                        Toast.makeText(
                            this@OutgoingInvitationActivity,
                            response.message(), Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(
                        this@OutgoingInvitationActivity,
                        t.message, Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            })
    }

    private fun cancelInvitation(receiverToken: String) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE)
            data.put(REMOTE_MSG_INVITATION_RESPONSE, REMOTE_MSG_INVITATION_CANCELLED)

            body.put(REMOTE_MSG_DATA, data)
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(), REMOTE_MSG_INVITATION_RESPONSE)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                e.message, Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!=null){
                val type: String? = intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE)
                if(type!=null){
                    when(type){
                        REMOTE_MSG_INVITATION_ACCEPTED -> {
                            Toast.makeText(
                                context,
                                "Invitation Accepted", Toast.LENGTH_LONG
                            ).show()
                        }
                        REMOTE_MSG_INVITATION_REJECTED -> {
                            Toast.makeText(
                                context,
                                "Invitation Rejected", Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(REMOTE_MSG_INVITATION_RESPONSE)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(
            invitationResponseReceiver
        )
    }

}
