package com.example.ghostbrowse

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import java.io.File

class IsolatedPdfRenderer(private val context: Context) {
    private var pdfRenderer: PdfRenderer? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    // Open and render a PDF from a session-specific file
    fun renderPdf(pdfFile: File, imageView: ImageView, pageNumber: Int = 0) {
        parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(parcelFileDescriptor!!)

        pdfRenderer?.let { renderer ->
            val page = renderer.openPage(pageNumber)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            imageView.setImageBitmap(bitmap)
            page.close()
        }
    }

    // Cleanup PDF resources
    fun destroySession() {
        pdfRenderer?.close()
        parcelFileDescriptor?.close()
    }
}