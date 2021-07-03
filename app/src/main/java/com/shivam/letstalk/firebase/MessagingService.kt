package com.shivam.letstalk.firebase

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shivam.letstalk.activities.IncomingInvitationActivity
import com.shivam.letstalk.custom.log
import com.shivam.letstalk.util.Constants
import com.shivam.letstalk.util.Constants.KEY_EMAIL
import com.shivam.letstalk.util.Constants.KEY_FIRST_NAME
import com.shivam.letstalk.util.Constants.KEY_LAST_NAME
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITATION_RESPONSE
import com.shivam.letstalk.util.Constants.REMOTE_MSG_INVITOR_TOKEN
import com.shivam.letstalk.util.Constants.REMOTE_MSG_MEETING_ROOM
import com.shivam.letstalk.util.Constants.REMOTE_MSG_MEETING_TYPE
import com.shivam.letstalk.util.Constants.REMOTE_MSG_TYPE

class MessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MessagingService:FCM"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        log("onMessageReceived123", "onMessageReceived")
        val type = remoteMessage.data.get(REMOTE_MSG_TYPE)
        if(type!=null){
            if(type == REMOTE_MSG_INVITATION){
                val intent = Intent(applicationContext, IncomingInvitationActivity::class.java)
                intent.putExtra(
                    REMOTE_MSG_MEETING_TYPE,
                    remoteMessage.data.get(REMOTE_MSG_MEETING_TYPE)
                )
                intent.putExtra(
                    KEY_FIRST_NAME,
                    remoteMessage.data.getValue(KEY_FIRST_NAME)
                )
                intent.putExtra(
                    KEY_LAST_NAME,
                    remoteMessage.data.getValue(KEY_LAST_NAME)
                )
                intent.putExtra(
                    KEY_EMAIL,
                    remoteMessage.data.getValue(KEY_EMAIL)
                )
                intent.putExtra(
                    REMOTE_MSG_MEETING_ROOM,
                    remoteMessage.data.getValue(REMOTE_MSG_MEETING_ROOM)
                )
                intent.putExtra(
                    REMOTE_MSG_INVITOR_TOKEN,
                    remoteMessage.data.getValue(REMOTE_MSG_INVITOR_TOKEN)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else if(type == REMOTE_MSG_INVITATION_RESPONSE){
                val intent = Intent(REMOTE_MSG_INVITATION_RESPONSE)
                intent.putExtra(
                    REMOTE_MSG_INVITATION_RESPONSE,
                    remoteMessage.data.get(REMOTE_MSG_INVITATION_RESPONSE)
                )
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


}