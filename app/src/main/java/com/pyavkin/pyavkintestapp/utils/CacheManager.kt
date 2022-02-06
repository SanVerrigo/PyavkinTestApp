package com.pyavkin.pyavkintestapp.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.InputStream

class CacheManager(private val context: Context) {

    fun createCacheFile(fileName: String): File {
        val directory = context.cacheDir
        return File(directory, fileName)
    }

    fun getUriByFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context, context.applicationContext.packageName + GIF_PROVIDER, file
        )
    }

    fun writeToFile(file: File, inputStream: InputStream) {
        file.writeBytes(inputStream.readBytes())
        while (getDirSizeInBytes(context.cacheDir) > MAX_SIZE) {
            deleteTheLatestFile(context.cacheDir)
        }
    }

    private fun deleteTheLatestFile(dir: File) {
        var latestFile: File? = null
        for (each in dir.listFiles()) {
            if (each.isFile) {
                if (latestFile == null || each.lastModified() < latestFile.lastModified()) {
                    latestFile = each
                }
            }
        }
        latestFile?.delete()
    }

    private fun getDirSizeInBytes(dir: File): Long {
        var size: Long = 0
        for (file in dir.listFiles()) {
            if (file != null && file.isDirectory) {
                size += getDirSizeInBytes(file)
            } else if (file != null && file.isFile) {
                size += file.length()
            }
        }
        return size
    }

    companion object {
        const val MAX_SIZE = 1024 * 1024 * 150L

        const val GIF_PROVIDER = ".gifprovider"
    }
}
