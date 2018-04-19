package com.example.jati.imagecompression.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.os.Environment
import android.util.Base64
import com.example.jati.imagecompression.data.CompressedImage
import com.example.jati.imagecompression.data.ImageData
import java.io.*

/**
 * Created by jati on 18/04/18
 */

object CreateImageUtils {
    fun createFile(): ImageData {
        val dir = File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DCIM.toString() + File.separator + Constants.IMAGE_DIR)
        val image = File.createTempFile(Constants.PHOTO_PREFIX, Constants.EXTENSIONS_JPG, dir)
        return ImageData(image.absolutePath, image)
    }

    fun compressImage(path: String): CompressedImage {
        val photo = File(path)
        val fileInputStream = FileInputStream(photo)
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = 2
        val original = BitmapFactory.decodeFile(path)
        val compressed = BitmapFactory.decodeStream(fileInputStream, Rect(), bitmapOptions)
        fileInputStream.close()
        writeBase64(original, false)
        writeBase64(compressed, true)
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

    private fun writeBase64(bitmap: Bitmap, isCompressed: Boolean) {
        val dir = Environment.getExternalStorageDirectory()
        val file = if (isCompressed) File(dir, "compressedBase64.txt") else File(dir, "originalBase64.txt")
        file.createNewFile()
        val fos = FileOutputStream(file)
        val osw = OutputStreamWriter(fos)
        val compBaos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, compBaos)
        osw.write(Base64.encodeToString(compBaos.toByteArray(), Base64.DEFAULT))
        osw.close()
    }
}