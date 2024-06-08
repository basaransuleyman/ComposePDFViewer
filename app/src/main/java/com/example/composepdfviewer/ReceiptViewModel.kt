package com.example.composepdfviewer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(): ViewModel() {

    private val _pdfFileState = MutableStateFlow<File?>(null)
    val pdfFileState: StateFlow<File?> = _pdfFileState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun downloadPdf(fileUrl: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val file = withContext(Dispatchers.IO) {
                    downloadedPdf(fileUrl, context)
                }
                _pdfFileState.value = file
            } catch (e: Exception) {
                e.printStackTrace()
                _pdfFileState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun downloadedPdf(fileUrl: String, context: Context): File {
        return withContext(Dispatchers.IO) {
            val url = URL(fileUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("Failed to download file")
            }

            val file = createNamedTempFile(context, "Dekont", ".pdf")
            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        }
    }

    @Throws(IOException::class)
    fun createNamedTempFile(context: Context, fileName: String, fileExtension: String): File {
        val tempDir = context.cacheDir
        val tempFile = File(tempDir, "$fileName$fileExtension")
        if (tempFile.exists()) {
            tempFile.delete()
        }
        tempFile.createNewFile()
        return tempFile
    }

    companion object {
        const val PDF_URL = "https://basaransuleyman.github.io/receiptsample/Frame%2018.pdf"
    }
}