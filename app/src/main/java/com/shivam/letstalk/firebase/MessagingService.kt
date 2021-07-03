package com.shivam.letstalk.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shivam.letstalk.custom.log

class MessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MessagingService:FCM"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


}