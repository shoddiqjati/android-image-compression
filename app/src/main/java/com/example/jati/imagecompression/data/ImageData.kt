package com.example.jati.imagecompression.data

import android.graphics.Bitmap
import java.io.File

/**
 * Created by jati on 18/04/18
 */
 
data class ImageData (
        val path: String,
        val image: File
)

data class CompressedImage(
        val photo: Bitmap,
        val size: Int,
        val height: Int,
        val width: Int,
        val orientation: String,
        val originalSize: Int,
        val originalHeight: Int,
        val originalWidth: Int
)