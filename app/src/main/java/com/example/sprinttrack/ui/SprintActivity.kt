package com.example.sprinttrack.ui

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sprinttrack.databinding.ActivitySprintBinding
import com.example.sprinttrack.firebase.FirebaseConfig
import com.example.sprinttrack.model.Treino
import com.example.sprinttrack.sensor.MotionSensorManager
import com.example.sprinttrack.sensor.StepCounterManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SprintActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySprintBinding

    private lateinit var motionSensor: MotionSensorManager
    private lateinit var stepCounter: StepCounterManager

    private var iniciou = false
    private var passos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySprintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SENSOR DE MOVIMENTO
        motionSensor = MotionSensorManager(this) {

            runOnUiThread {

                if (!iniciou) {

                    iniciou = true

                    binding.chronometer.base =
                        SystemClock.elapsedRealtime()

                    binding.chronometer.start()

                    Toast.makeText(
                        this,
                        "Sprint iniciado!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // CONTADOR DE PASSOS
        stepCounter = StepCounterManager(this) { totalPassos ->

            passos = totalPassos

            runOnUiThread {

                binding.txtPassos.text =
                    "Passos: $passos"
            }
        }

        binding.btnPreparar.setOnClickListener {

            Toast.makeText(
                this,
                "Corra para iniciar!",
                Toast.LENGTH_SHORT
            ).show()

            motionSensor.start()
            stepCounter.start()
        }

        binding.btnFinalizar.setOnClickListener {

            finalizarTreino()
        }
    }

    private fun finalizarTreino() {

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
            observacao = "",
            fotoUrl = ""
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

                finish()
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        motionSensor.stop()
        stepCounter.stop()
    }
}