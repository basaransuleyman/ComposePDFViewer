package com.example.composepdfviewer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composepdfviewer.ReceiptViewModel.Companion.PDF_URL
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreen(viewModel: ReceiptViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val pdfFile by viewModel.pdfFileState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.downloadPdf(PDF_URL, context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("İşlem Dekontu") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back press */ }) {
                        Icon(painterResource(id = android.R.drawable.ic_menu_close_clear_cancel), contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (pdfFile != null) {
                FloatingActionButton(onClick = { sharePdf(context, pdfFile!!) }) {
                    Text("Dekont Paylaş")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                pdfFile?.let { file ->
                    PdfViewer(file = file, modifier = Modifier.fillMaxSize())
                } ?: Text("No PDF available")
            }
        }
    }
}

fun sharePdf(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
}