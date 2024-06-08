package com.example.composepdfviewer

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


@Composable
fun PdfViewer(file: File, modifier: Modifier = Modifier) {
    var currentPageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    LaunchedEffect(file) {
        withContext(Dispatchers.IO) {
            val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val currentPage = pdfRenderer.openPage(0)

            val displayMetrics = context.resources.displayMetrics
            val pageWidth = displayMetrics.widthPixels
            val pageHeight = (pageWidth * currentPage.height / currentPage.width)

            val bitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888)
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            currentPageBitmap = bitmap

            currentPage.close()
            pdfRenderer.close()
            parcelFileDescriptor.close()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 2f)
                    offsetX = (offsetX + pan.x * scale).coerceIn(-(scale - 1) * currentPageBitmap!!.width / 2, (scale - 1) * currentPageBitmap!!.width / 2)
                    offsetY = (offsetY + pan.y * scale).coerceIn(-(scale - 1) * currentPageBitmap!!.height / 2, (scale - 1) * currentPageBitmap!!.height / 2)
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
    ) {
        currentPageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}