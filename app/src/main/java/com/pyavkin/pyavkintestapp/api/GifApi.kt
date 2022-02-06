package com.pyavkin.pyavkintestapp.api

import com.pyavkin.pyavkintestapp.api.entities.GifEntity
import com.pyavkin.pyavkintestapp.api.entities.GifResult
import io.reactivex.rxjava3.core.Single
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GifApi {

    @GET("{section}/{page}?json=true")
    fun getGifUrls(
        @Path("section") section: String,
        @Path("page") page: Int,
    ): Single<GifResult>

    @GET("random?json=true")
    fun getRandomGif(): Single<GifEntity>

    @GET
    @Streaming
    fun downloadGif(
        @Url url: String,
    ): Single<ResponseBody>
}
