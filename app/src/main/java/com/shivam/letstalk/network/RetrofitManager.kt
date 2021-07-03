package com.shivam.letstalk.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


const val BASE_URL = "https://fcm.googleapis.com/fcm/"
const val API_KEY = "1bc3bd2b117b4595a083c24ddcc9dc82"

object RetrofitManager{
    val newsInstance: ApiInterface
    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder().
        baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(client)
            .build()
        newsInstance = retrofit.create(ApiInterface::class.java)
    }
}