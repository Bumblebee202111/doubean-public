package com.github.bumblebee202111.doubean.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
    suspend fun saveImage(imageUrl: String): Result<String> {
        val snapshot = imageLoader.diskCache?.openSnapshot(imageUrl)
            ?: return Result.failure(NoSuchElementException())

        return snapshot.use {
            val cachedFile = it.data.toFile()
            val filename =
                imageUrl.toUri().lastPathSegment ?: "douban_image_${System.currentTimeMillis()}.jpg"

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_PICTURES}/Douban"
                        )
                    }

                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    ) ?: throw IOException("Failed to create MediaStore record.")

                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        cachedFile.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    } ?: throw IOException("Failed to open output stream.")

                    Result.success("Pictures/Douban/$filename")
                } else {
                    val doubanDir = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "Douban"
                    ).apply { mkdirs() }

                    val destinationFile = File(doubanDir, filename)
                    cachedFile.copyTo(destinationFile, overwrite = true)

                    Result.success(destinationFile.absolutePath)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}