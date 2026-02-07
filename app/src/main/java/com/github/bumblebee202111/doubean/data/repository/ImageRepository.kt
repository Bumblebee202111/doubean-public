package com.github.bumblebee202111.doubean.data.repository

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import coil3.ImageLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

class ImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) {
    suspend fun saveImage(imageUrl: String): Result<File> {
        val snapshot = imageLoader.diskCache?.openSnapshot(imageUrl)
            ?: return Result.failure(IOException())

        return snapshot.use {
            val cachedFile = it.data.toFile()
            val filename =
                imageUrl.toUri().lastPathSegment ?: "douban_image_${System.currentTimeMillis()}.jpg"
            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val doubanDir = File(picturesDir, "Douban").apply { mkdirs() }
            val destinationFile = File(doubanDir, filename)

            try {
                cachedFile.copyTo(destinationFile, overwrite = true)
                Result.success(destinationFile)
            } catch (e: IOException) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}