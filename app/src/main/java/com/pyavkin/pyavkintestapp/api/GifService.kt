package com.pyavkin.pyavkintestapp.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pyavkin.pyavkintestapp.R
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GifService(context: Context) {

    val api: GifApi

    init {
        val client = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.base_url))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        api = retrofit.create(GifApi::class.java)
    }

    private fun createGson(): Gson {
        return GsonBuilder()
            .create()
    }
}
