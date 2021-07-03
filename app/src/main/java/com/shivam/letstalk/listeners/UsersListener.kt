package com.shivam.letstalk.listeners

import com.shivam.letstalk.models.User

open interface UsersListener {
    fun initiateVideoMeeting(user: User)
    fun initiateAudioMeeting(user: User)
 }