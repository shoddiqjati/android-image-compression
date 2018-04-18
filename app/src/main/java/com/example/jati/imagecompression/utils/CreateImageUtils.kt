package com.example.jati.imagecompression.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.os.Environment
import com.example.jati.imagecompression.data.CompressedImage
import com.example.jati.imagecompression.data.ImageData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by jati on 18/04/18
 */

object CreateImageUtils {
    fun createFile(context: Context): ImageData {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(Constants.PHOTO_PREFIX, Constants.EXTENSIONS_JPG, storageDir)
        return ImageData(image.absolutePath, image)
    }

    fun compressImage(path: String): CompressedImage {
        val millis = System.currentTimeMillis()
        val photo = File(path)
        val fileInputStream = FileInputStream(photo)
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = 2
        val original = BitmapFactory.decodeFile(path)
        val compressed = BitmapFactory.decodeStream(fileInputStream, Rect(), bitmapOptions)
        fileInputStream.close()
        val photoName = "COMP_IC_$millis.jpg"
        val compressedPhoto = File(Environment.getExternalStorageDirectory(), photoName)
        val fileOutputStream = FileOutputStream(compressedPhoto)
        compressed.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileInputStream.close()
        val originalExif = ExifInterface(photo.absolutePath)
        val rotation = getRotationDegrees(originalExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL))
        val rotatedBitmap = Bitmap.createBitmap(compressed, 0, 0, compressed.width, compressed.height, rotatePhoto(rotation.toFloat()), true)

        return CompressedImage(rotatedBitmap, rotatedBitmap.byteCount, rotatedBitmap.height, rotatedBitmap.width, getOrientationName(rotation),
                original.byteCount, original.height, original.width)
    }

    private fun getRotationDegrees(rotation: Int): Int {
        return when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    private fun rotatePhoto(rotation: Float): Matrix {
        val matrix = Matrix()
        if (rotation != 0f) matrix.postRotate(rotation)
        return matrix
    }

    private fun getOrientationName(rotation: Int): String {
        return when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> Constants.DEGREE_90
            ExifInterface.ORIENTATION_ROTATE_180 -> Constants.DEGREE_180
            ExifInterface.ORIENTATION_ROTATE_270 -> Constants.DEGREE_270
            else -> Constants.NORMAL
        }
    }
}