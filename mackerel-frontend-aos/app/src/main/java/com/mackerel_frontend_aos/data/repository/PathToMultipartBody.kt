package com.mackerel_frontend_aos.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat

object PathToMultipartBody {
    //Path to Bitmap
    fun pathToBitmap(path: String?): Bitmap? {
        return try {
            val f = File(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            BitmapFactory.decodeStream(FileInputStream(f), null, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //Bitmap Orientation
    private fun getOrientationOfImage(filepath: String): Int? {
        var exif: ExifInterface? = null
        var result: Int? = null

        try {
            exif = ExifInterface(filepath)
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }

        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
        if (orientation != -1) {
            result = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        }
        return result
    }

    //Bitmap Rotate
    fun rotatedBitmap(bitmap: Bitmap?, filepath: String): Bitmap? {
        val matrix = Matrix()
        var resultBitmap: Bitmap? = null

        when (getOrientationOfImage(filepath)) {
            0 -> matrix.setRotate(0F)
            90 -> matrix.setRotate(90F)
            180 -> matrix.setRotate(180F)
            270 -> matrix.setRotate(270F)
        }

        resultBitmap = try {
            bitmap?.let { Bitmap.createBitmap(it, 0, 0, bitmap.width, bitmap.height, matrix, true) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return resultBitmap
    }

    //Bitmap Resize
    fun resizeBitmapImage(source: Bitmap, maxResolution: Int): Bitmap {
        val width = source.width
        val height = source.height
        var newWidth = width
        var newHeight = height
        var rate = 0.0f
        if (width > height) {
            if (maxResolution < width) {
                rate = maxResolution / width.toFloat()
                newHeight = (height * rate).toInt()
                newWidth = maxResolution
            }
        } else {
            if (maxResolution < height) {
                rate = maxResolution / height.toFloat()
                newWidth = (width * rate).toInt()
                newHeight = maxResolution
            }
        }
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }

    //Bitmap to Uri
    fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext?.contentResolver,
            inImage,
            newJpgFileName(),
            null
        )
        return Uri.parse(path)
    }

    //이미지 시간 포맷으로 변경
    fun newJpgFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        return "${filename}.jpg"
    }
}