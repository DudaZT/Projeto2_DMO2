package com.example.sprinttrack.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun bitmapToBase64(bitmap: Bitmap): String {
        val maxDimensao = 400
        val larguraOriginal = bitmap.width
        val alturaOriginal = bitmap.height

        val (novaLargura, novaAltura) = if (larguraOriginal > alturaOriginal) {
            val taxa = maxDimensao.toFloat() / larguraOriginal
            Pair(maxDimensao, (alturaOriginal * taxa).toInt())
        } else {
            val taxa = maxDimensao.toFloat() / alturaOriginal
            Pair((larguraOriginal * taxa).toInt(), maxDimensao)
        }

        val bitmapReduzido = Bitmap.createScaledBitmap(bitmap, novaLargura, novaAltura, true)
        val outputStream = ByteArrayOutputStream()
        bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
}