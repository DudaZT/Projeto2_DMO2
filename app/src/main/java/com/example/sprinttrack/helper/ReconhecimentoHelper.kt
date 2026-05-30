package com.example.sprinttrack.helper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/**
 * Helper para reconhecimento de voz.
 * Encapsula toda a lógica do SpeechRecognizer do Android.
 * O ReconhecimentoHelper encapsula toda a complexidade da API SpeechRecognizer.
 * Ele configura o idioma, inicia a escuta e devolve o texto reconhecido via callback.
 * Tratamos cada tipo de erro separadamente para feedback ao usuário.
 */
class ReconhecimentoHelper(
    private val context: Context,
    private val callback: Callback // Interface para devolver resultado ou erro
) {

    // Interface que a Activity implementa para receber os resultados
    interface Callback {

        fun onTextoReconhecido(texto: String)

        fun onErro(mensagem: String)
    }

    // Instância do reconhecedor de voz do sistema
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

    fun iniciarReconhecimento() {
        // Verifica se o dispositivo suporta reconhecimento de voz
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {

            callback.onErro(
                "Reconhecimento de voz não disponível"
            )

            return
        }

        // Configura a Intent com os parâmetros do reconhecimento
        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra( // Idioma português
            RecognizerIntent.EXTRA_LANGUAGE,
            "pt-BR"
        )

        intent.putExtra( // Texto exibido ao usuário
            RecognizerIntent.EXTRA_PROMPT,
            "Fale sua observação"
        )

        // Configura o listener que recebe os eventos do reconhecimento
        recognizer.setRecognitionListener(
            object : RecognitionListener {

                override fun onResults(results: Bundle?) {
                    // Pega a primeira frase reconhecida
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
                    // Mapeia códigos de erro
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

                // Callbacks obrigatórios da interface
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

    /**
     * Libera os recursos do reconhecedor quando não for mais necessário.
     */
    fun destruir() {

        recognizer.destroy()
    }
}