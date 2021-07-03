package com.shivam.letstalk.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shivam.letstalk.R
import com.shivam.letstalk.models.User
import com.shivam.letstalk.network.RetrofitManager
import com.shivam.letstalk.util.Constants
import com.shivam.letstalk.util.Constants.KEY_EMAIL
import com.shivam.letstalk.util.Constants.KEY_FIRST_NAME
import com.shivam.letstalk.util.Constants.KEY_LAST_NAME
import com.shivam.letstalk.util.Constants.REMOTE_MSG_DATA
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION_ACCEPTED
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION_CANCELLED
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION_RESPONSE
import com.shivam.letstalk.util.Constants.REMOTE_MSG_MEETING_ROOM
import com.shivam.letstalk.util.Constants.REMOTE_MSG_REGISTRATION_IDS
import com.shivam.letstalk.util.Constants.REMOTE_MSG_TYPE
import kotlinx.android.synthetic.main.activity_incoming_invitation.*
import kotlinx.android.synthetic.main.activity_incoming_invitation.ivMeetingType
import kotlinx.android.synthetic.main.activity_incoming_invitation.tvFirstChar
import kotlinx.android.synthetic.main.activity_outgoing_invitation.*
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL

class IncomingInvitationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_invitation)

        val meetingType = intent.getStringExtra("type")
        meetingType?.let {
            when (meetingType) {
                "video" -> ivMeetingType.setImageResource(R.drawable.ic_video)
            }
        }

        val firstName = intent.getStringExtra(KEY_FIRST_NAME)
        val lastName = intent.getStringExtra(KEY_LAST_NAME)
        val email = intent.getStringExtra(KEY_EMAIL)

        if (firstName != null)
            tvFirstChar.text = firstName.substring(0, 1)

        if (firstName != null && lastName != null)
            tvIncomingUserName.text = String.format("%s %s", firstName, lastName)

        if (email != null)
        tvIncomingUserEmail.text = email

        val receiverToken = intent.getStringExtra(Constants.REMOTE_MSG_INVITOR_TOKEN)
        ivAcceptInvitation.setOnClickListener {
            receiverToken?.let { receiverToken ->
                sendInvitationResponse(
                    REMOTE_MSG_INVITATION_ACCEPTED,
                    receiverToken
                )
            }
        }

        ivRejectInvitation.setOnClickListener {
            receiverToken?.let { receiverToken ->
                sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_REJECTED,
                    receiverToken
                )
            }
        }

    }

    private fun sendInvitationResponse(type: String, receiverToken: String) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(REMOTE_MSG_TYPE, REMOTE_MSG_INVITATION_RESPONSE)
            data.put(REMOTE_MSG_INVITATION_RESPONSE, type)

            body.put(REMOTE_MSG_DATA, data)
            body.put(REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(), type)
        } catch (e: Exception) {
            Toast.makeText(
                this@IncomingInvitationActivity,
                e.message, Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun sendRemoteMessage(remoteMessageBody: String, type: String) {
        RetrofitManager.newsInstance.sendRemoteMessage(
            Constants.getRemoteMessageHeaders(), remoteMessageBody
        )
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        if (type == REMOTE_MSG_INVITATION_ACCEPTED) {
                            try {
                                val serverURL: URL
                                serverURL = try {
                                    // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
                                    URL("https://meet.jit.si")
                                } catch (e: MalformedURLException) {
                                    e.printStackTrace()
                                    throw RuntimeException("Invalid server URL!")
                                }
                                val defaultOptions = JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverURL)
                                    // When using JaaS, set the obtained JWT here
                                    //.setToken("MyJWT")
                                    // Different features flags can be set
                                    //.setFeatureFlag("toolbox.enabled", false)
                                    //.setFeatureFlag("filmstrip.enabled", false)
                                    .setWelcomePageEnabled(false)
                                    .build()
                                JitsiMeet.setDefaultConferenceOptions(defaultOptions)

                                val options = JitsiMeetConferenceOptions.Builder()
                                    .setRoom("text")
                                    // Settings for audio and video
                                    //.setAudioMuted(true)
                                    //.setVideoMuted(true)
                                    .build()
                                // Launch the new activity with the given options. The launch() method takes care
                                // of creating the required Intent and passing the options.
                                JitsiMeetActivity.launch(this@IncomingInvitationActivity, options)
                                finish()

                            }catch (e: Exception){
                                Toast.makeText(
                                this@IncomingInvitationActivity,
                                e.message, Toast.LENGTH_LONG
                            ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@IncomingInvitationActivity,
                                "Invitation Rejected", Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@IncomingInvitationActivity,
                            response.message(), Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(
                        this@IncomingInvitationActivity,
                        t.message, Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            })
    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!=null){
                val type: String? = intent.getStringExtra(REMOTE_MSG_INVITATION_RESPONSE)
                if(type!=null){
                    when(type){
                        REMOTE_MSG_INVITATION_CANCELLED -> {
                            Toast.makeText(
                                context,
                                "Invitation Cancelled", Toast.LENGTH_LONG
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