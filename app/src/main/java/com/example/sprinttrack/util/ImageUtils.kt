package com.example.sprinttrack.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Utilitário para conversão de imagens entre Bitmap e Base64.
 * Usado para salvar a foto de perfil no Firestore como texto.
 * Como o Firestore não armazena arquivos binários grandes,
 * convertemos a foto para Base64.
 * O redimensionamento evita estourar o limite de 1MB por documento.
 */
object ImageUtils {

    /**
     * Converte um Bitmap para string Base64.
     * Antes da conversão, redimensiona a imagem para no máximo 400px
     * e aplica compressão JPEG com qualidade 80% para reduzir o tamanho.
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val maxDimensao = 400 // Tamanho máximo do lado maior
        val larguraOriginal = bitmap.width
        val alturaOriginal = bitmap.height

        // Calcula novas dimensões mantendo a proporção
        val (novaLargura, novaAltura) = if (larguraOriginal > alturaOriginal) {
            val taxa = maxDimensao.toFloat() / larguraOriginal
            Pair(maxDimensao, (alturaOriginal * taxa).toInt())
        } else {
            val taxa = maxDimensao.toFloat() / alturaOriginal
            Pair((larguraOriginal * taxa).toInt(), maxDimensao)
        }

        // Redimensiona a imagem
        val bitmapReduzido = Bitmap.createScaledBitmap(bitmap, novaLargura, novaAltura, true)

        // Comprime para JPEG e escreve em um array de bytes
        val outputStream = ByteArrayOutputStream()
        bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()

        // Codifica os bytes em Base64
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * Converte uma string Base64 de volta para Bitmap.
     * Retorna null se a string for inválida.
     */
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null // retorna null se a decodificação falhar
        }
    }
}