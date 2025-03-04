package com.saksham.modulemanager.util

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility class for downloading files
 */
object DownloadManager {
    private val client = OkHttpClient()
    
    /**
     * Download a file from a URL
     *
     * @param context The application context
     * @param url The URL to download from
     * @param filename The name to save the file as
     * @param onProgressUpdate Callback for progress updates
     * @return Result containing the downloaded file or an error
     */
    suspend fun downloadFile(
        context: Context,
        url: String,
        filename: String,
        onProgressUpdate: (Float) -> Unit = {}
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Unexpected response code: ${response.code}"))
            }
            
            val body = response.body ?: return@withContext Result.failure(IOException("Empty response body"))
            val contentLength = body.contentLength()
            
            val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir
            
            val file = File(downloadDir, filename)
            
            FileOutputStream(file).use { outputStream ->
                body.byteStream().use { inputStream ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    var totalBytesRead = 0L
                    
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        
                        if (contentLength > 0) {
                            val progress = totalBytesRead.toFloat() / contentLength.toFloat()
                            onProgressUpdate(progress)
                        }
                    }
                }
            }
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
