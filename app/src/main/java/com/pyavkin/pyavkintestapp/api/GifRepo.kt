package com.pyavkin.pyavkintestapp.api

import android.net.Uri
import com.pyavkin.pyavkintestapp.api.entities.GifEntity
import io.reactivex.rxjava3.core.Single

interface GifRepo {

    fun getGifs(section: String, page: Int): Single<List<GifEntity>>

    fun downloadGif(url: String, id: Long, section: String, name: String): Single<Uri>
}
