package com.example.sprinttrack.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.sprinttrack.R
import com.example.sprinttrack.databinding.ActivitySprintBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.model.Treino
import com.example.sprinttrack.sensor.MotionSensorManager
import com.example.sprinttrack.sensor.StepCounterManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.sprinttrack.helper.ReconhecimentoHelper

/**
 * Tela principal do treino.
 * Integra os 3 recursos principais: sensores de movimento, microfone e notificação.
 * Implementa a interface ReconhecimentoHelper. Callback para receber o texto ditado.
 * Esta é a tela mais importante do app.
 * Ela integra os 3 recursos: sensores de movimento para detectar a corrida
 * e contar passos, microfone para ditar observações,
 * e sons + notificação como feedback.
 * O fluxo é: preparar → contagem regressiva com som → sprint com sensores ativos →
 * finalizar com som de chegada → salvar no Firestore com notificação.
 */
class SprintActivity :
    AppCompatActivity(),
    ReconhecimentoHelper.Callback {

    private lateinit var binding: ActivitySprintBinding

    // Gerenciadores dos sensores
    private lateinit var motionSensor: MotionSensorManager
    private lateinit var stepCounter: StepCounterManager

    // Estado do treino
    private var iniciou = false
    private var passos = 0

    // Players para os sons de início e fim
    private var startPlayer: MediaPlayer? = null
    private var finishPlayer: MediaPlayer? = null

    // Helper para reconhecimento de voz
    private lateinit var reconhecimentoHelper: ReconhecimentoHelper

    // Permissão de sensores de atividade física
    private val requestActivityPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {

            iniciarContagem()

        } else {

            Toast.makeText(
                this,
                "Permissão dos sensores negada",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Permissão do microfone
    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {

            abrirReconhecimentoVoz()

        } else {

            Toast.makeText(
                this,
                "Permissão do microfone negada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Permissão de notificações
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (!isGranted) {

                Toast.makeText(
                    this,
                    "Permissão de notificação negada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySprintBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Inicializa o helper de voz passando 'this' como callback
        reconhecimentoHelper = ReconhecimentoHelper(this, this)

        verificarPermissaoNotificacao()

        criarCanalNotificacao()

        configurarSensores()

        configurarBotoes()
    }

    /**
     * notificações precisam de permissão explícita do usuário.
     */
    private fun verificarPermissaoNotificacao() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestNotificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    /**
     * Cria o canal de notificação
     */
    private fun criarCanalNotificacao() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "sprint_track",
                "Sprint Track",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.description =
                "Notificações dos treinos"

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }
    /**
     * Inicializa os gerenciadores de sensores com seus respectivos callbacks.
     */
    private fun configurarSensores() {
        // MotionSensor: o callback está vazio porque usamos apenas para registro
        motionSensor = MotionSensorManager(this) {
            runOnUiThread {
                binding.txtStatus.text = "Correndo 🚀"
            }
        }
        // StepCounter: atualiza o TextView de passos na thread principal
        stepCounter = StepCounterManager(this) { totalPassos ->

            passos = totalPassos

            runOnUiThread {

                binding.txtPassos.text =
                    "Passos: $passos"
            }
        }
    }

    /**
     * Vincula os botões da interface às suas ações.
     */
    private fun configurarBotoes() {

        binding.btnPreparar.setOnClickListener {

            verificarPermissaoSensores()
        }

        binding.btnFinalizar.setOnClickListener {

            if (!iniciou) {

                Toast.makeText(
                    this,
                    "A sprint ainda não começou",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            finalizarTreino()
        }

        binding.btnMicrofone.setOnClickListener {

            verificarPermissaoMicrofone()
        }
    }

    /**
     * Verifica permissão de atividade física
     */
    private fun verificarPermissaoSensores() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                iniciarContagem()

            } else {

                requestActivityPermissionLauncher.launch(
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            }

        } else {

            iniciarContagem()
        }
    }

    // CONTAGEM REGRESSIVA + SOM
    /**
     * Inicia a contagem regressiva de 3 segundos com som de largada.
     * Desabilita o botão para evitar múltiplos cliques.
     */
    private fun iniciarContagem() {

        binding.btnPreparar.isEnabled = false

        Toast.makeText(
            this,
            "Preparar...",
            Toast.LENGTH_SHORT
        ).show()

        // Toca o som de preparação
        startPlayer?.release()

        startPlayer = MediaPlayer.create(
            this,
            R.raw.race_start
        )

        startPlayer?.start()

        // ESPERA 3 SEGUNDOS
        // Agenda o início após 3 segundos
        Handler(Looper.getMainLooper()).postDelayed({

            iniciarSprint()

        }, 3000)
    }

    /**
     * Inicia efetivamente o treino:
     * - Dispara o cronômetro
     * - Ativa acelerômetro e contador de passos
     */
    private fun iniciarSprint() {

        iniciou = true

        // Cronômetro: base é o tempo atual decorrido desde o boot
        binding.chronometer.base =
            SystemClock.elapsedRealtime()

        binding.chronometer.start()

        motionSensor.start()

        stepCounter.start()

        Toast.makeText(
            this,
            "GO GO GO!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun verificarPermissaoMicrofone() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            abrirReconhecimentoVoz()

        } else {

            requestAudioPermissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    private fun abrirReconhecimentoVoz() {

        reconhecimentoHelper.iniciarReconhecimento()
    }

    /**
     * Para os sensores, calcula o tempo, busca dados do usuário e salva no Firestore.
     */
    private fun finalizarTreino() {
        // Som de chegada
        finishPlayer?.release()
        finishPlayer = MediaPlayer.create(this, R.raw.finish)
        finishPlayer?.start()

        // Para sensores e cronômetro
        binding.chronometer.stop()
        motionSensor.stop()
        stepCounter.stop()

        // Calcula tempo decorrido
        val tempoMillis = SystemClock.elapsedRealtime() - binding.chronometer.base
        val tempoSegundos = tempoMillis / 1000.0
        val uid = FirebaseConfig.auth.currentUser?.uid ?: ""
        val dataAtual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        // 1. Busca os dados de Perfil (Nome e Foto) antes de salvar o treino
        if (uid.isNotEmpty()) {
            FirebaseConfig.firestore
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->

                    val nomeUsuario = document.getString("nome") ?: "Corredor Anônimo"
                    val fotoUsuario = document.getString("foto") ?: ""

                    // 2. Monta o dicionário completo do Treino contendo os dados do atleta
                    val mapaTreino = hashMapOf(
                        "uid" to uid,
                        "tempo" to tempoSegundos,
                        "passos" to passos,
                        "data" to dataAtual,
                        "observacao" to binding.edtObservacao.text.toString(),
                        "fotoUrl" to "",
                        "timestamp" to System.currentTimeMillis(),
                        "nome" to nomeUsuario,  // Campo lido pela Leaderboard
                        "foto" to fotoUsuario   // Campo de imagem Base64 lido pela Leaderboard
                    )

                    // 3. Persiste o treino atualizado no nó global de treinos
                    FirebaseConfig.firestore
                        .collection("treinos")
                        .add(mapaTreino)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Treino salvo!", Toast.LENGTH_SHORT).show()
                            mostrarNotificacao()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao salvar treino", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao conectar com perfil do usuário", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Exibe uma notificação local confirmando que o treino foi salvo.
     */
    private fun mostrarNotificacao() {

        val notification =
            NotificationCompat.Builder(this, "sprint_track")
                .setContentTitle("Treino finalizado")
                .setContentText(
                    "Seu sprint foi salvo com sucesso!"
                )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

        val manager =
            getSystemService(NotificationManager::class.java)

        manager.notify(1, notification)
    }

    /**
     * Quando a voz é reconhecida, preenche automaticamente o campo de observação.
     */
    override fun onTextoReconhecido(texto: String) {

        binding.edtObservacao.setText(texto)

        Toast.makeText(
            this,
            "Voz reconhecida!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onErro(mensagem: String) {

        Toast.makeText(
            this,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Libera todos os recursos: sensores, players de áudio e reconhecedor de voz.
     */
    override fun onDestroy() {
        super.onDestroy()

        motionSensor.stop()

        stepCounter.stop()

        startPlayer?.release()

        finishPlayer?.release()

        reconhecimentoHelper.destruir()
    }
}