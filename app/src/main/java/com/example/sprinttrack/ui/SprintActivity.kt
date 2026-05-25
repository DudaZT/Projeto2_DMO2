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
class SprintActivity :
    AppCompatActivity(),
    ReconhecimentoHelper.Callback {

    private lateinit var binding: ActivitySprintBinding

    private lateinit var motionSensor: MotionSensorManager
    private lateinit var stepCounter: StepCounterManager

    private var iniciou = false
    private var passos = 0

    private var startPlayer: MediaPlayer? = null
    private var finishPlayer: MediaPlayer? = null

    private lateinit var reconhecimentoHelper: ReconhecimentoHelper

    // PERMISSÃO SENSOR
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

    // PERMISSÃO MICROFONE
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

        reconhecimentoHelper = ReconhecimentoHelper(this, this)

        verificarPermissaoNotificacao()

        criarCanalNotificacao()

        configurarSensores()

        configurarBotoes()
    }

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
    private fun configurarSensores() {

        motionSensor = MotionSensorManager(this) {}

        stepCounter = StepCounterManager(this) { totalPassos ->

            passos = totalPassos

            runOnUiThread {

                binding.txtPassos.text =
                    "Passos: $passos"
            }
        }
    }

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
    private fun iniciarContagem() {

        binding.btnPreparar.isEnabled = false

        Toast.makeText(
            this,
            "Preparar...",
            Toast.LENGTH_SHORT
        ).show()

        startPlayer?.release()

        startPlayer = MediaPlayer.create(
            this,
            R.raw.race_start
        )

        startPlayer?.start()

        // ESPERA 3 SEGUNDOS
        Handler(Looper.getMainLooper()).postDelayed({

            iniciarSprint()

        }, 3000)
    }

    private fun iniciarSprint() {

        iniciou = true

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

    private fun finalizarTreino() {

        finishPlayer?.release()

        finishPlayer = MediaPlayer.create(
            this,
            R.raw.finish
        )

        finishPlayer?.start()

        binding.chronometer.stop()

        motionSensor.stop()

        stepCounter.stop()

        val tempoMillis =
            SystemClock.elapsedRealtime() -
                    binding.chronometer.base

        val tempoSegundos =
            tempoMillis / 1000.0

        val uid =
            FirebaseConfig.auth.currentUser?.uid ?: ""

        val dataAtual =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

        val treino = Treino(
            uid = uid,
            tempo = tempoSegundos,
            passos = passos,
            data = dataAtual,
            observacao =
                binding.edtObservacao.text.toString(),
            fotoUrl = "",
            timestamp = System.currentTimeMillis()
        )

        FirebaseConfig.firestore
            .collection("treinos")
            .add(treino)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Treino salvo!",
                    Toast.LENGTH_SHORT
                ).show()

                mostrarNotificacao()

                finish()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Erro ao salvar treino",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

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

    override fun onDestroy() {
        super.onDestroy()

        motionSensor.stop()

        stepCounter.stop()

        startPlayer?.release()

        finishPlayer?.release()

        reconhecimentoHelper.destruir()
    }
}