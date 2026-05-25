package com.example.sprinttrack.helper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class ReconhecimentoHelper(
    private val context: Context,
    private val callback: Callback
) {

    interface Callback {

        fun onTextoReconhecido(texto: String)

        fun onErro(mensagem: String)
    }

    private val recognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    fun iniciarReconhecimento() {

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {

            callback.onErro(
                "Reconhecimento de voz não disponível"
            )

            return
        }

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "pt-BR"
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Fale sua observação"
        )

        recognizer.setRecognitionListener(
            object : RecognitionListener {

                override fun onResults(results: Bundle?) {

                    val texto =
                        results
                            ?.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION
                            )
                            ?.firstOrNull()

                    if (texto != null) {

                        callback.onTextoReconhecido(texto)

                    } else {

                        callback.onErro(
                            "Nenhuma voz reconhecida"
                        )
                    }
                }

                override fun onError(error: Int) {

                    val mensagem = when (error) {

                        SpeechRecognizer.ERROR_NETWORK ->
                            "Erro de internet"

                        SpeechRecognizer.ERROR_NO_MATCH ->
                            "Nenhuma fala reconhecida"

                        SpeechRecognizer.ERROR_AUDIO ->
                            "Erro no microfone"

                        SpeechRecognizer.ERROR_CLIENT ->
                            "Erro no reconhecimento"

                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                            "Permissão negada"

                        else ->
                            "Erro ao reconhecer voz"
                    }

                    callback.onErro(mensagem)
                }

                override fun onReadyForSpeech(params: Bundle?) {}

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {}

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }
        )

        recognizer.startListening(intent)
    }

    fun destruir() {

        recognizer.destroy()
    }
}