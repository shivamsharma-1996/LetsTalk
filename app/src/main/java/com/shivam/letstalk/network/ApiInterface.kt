package com.shivam.letstalk.network

import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @POST("send")
    fun sendRemoteMessage(@HeaderMap headerMap: HashMap<String, String>,
    @Body remoteBody: String) : Call<String>
}