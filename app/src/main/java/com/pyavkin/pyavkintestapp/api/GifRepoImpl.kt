package com.pyavkin.pyavkintestapp.api

import android.content.Context
import android.net.Uri
import com.pyavkin.pyavkintestapp.api.entities.GifEntity
import com.pyavkin.pyavkintestapp.utils.CacheManager
import cz.jirutka.unidecode.Unidecode
import io.reactivex.rxjava3.core.Single

class GifRepoImpl(private val context: Context) : GifRepo {

    private val gifService = GifService(context)
    private val unidecode = Unidecode.toAscii()
    private val cacheManager by lazy {
        CacheManager(context)
    }

    override fun getGifs(section: String, page: Int): Single<List<GifEntity>> {
        return gifService.api.getGifUrls(section, page)
            .map { gifResult -> gifResult.result }
    }

    override fun getRandomGif(): Single<GifEntity> {
        return gifService.api.getRandomGif()
    }

    override fun downloadGif(url: String, id: Long, section: String, name: String): Single<Uri> {
        val fileName = getGifName(name, id)
        val file = cacheManager.createCacheFile(fileName)
        val uri = cacheManager.getUriByFile(file)
        return if (file.exists()) {
            Single.fromCallable { uri }
        } else {
            gifService.api.downloadGif(url)
                .map { httpResponse ->
                    cacheManager.writeToFile(file, httpResponse.byteStream())
                    return@map uri
                }
        }
    }

    private fun getGifName(name: String, id: Long): String {
        return "${
            unidecode.decode(
                name.replace(' ', '_')
                    .replace(".", "")
            )
        }_$id.gif"
    }
}
