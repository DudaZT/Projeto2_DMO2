package com.example.sprinttrack.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Gerencia o sensor contador de passos do dispositivo.
 * Como o sensor retorna o total acumulado desde o boot,
 * precisamos guardar o valor inicial para calcular apenas os passos da sessão.
 * O sensor de passos retorna o total desde que o celular foi ligado.
 * Por isso guardamos o valor inicial e subtraímos a cada leitura
 * assim mostramos apenas os passos da corrida atual.
 */
class StepCounterManager(
    context: Context,
    private val onStepsChanged: (Int) -> Unit // Callback que recebe a contagem atual
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager

    // Sensor de contador de passos (hardware dedicado em muitos dispositivos)
    private val stepSensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        )

    private var passosIniciais = -1 // Valor sentinela: -1 = ainda não inicializado

    fun start() {
        // Se o dispositivo não tem sensor de passos, simplesmente não ativa
        if (stepSensor == null) {
            return
        }

        sensorManager.registerListener(
            this,
            stepSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {

        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val total = event?.values?.get(0)?.toInt() ?: 0 // Total acumulado desde o boot

        // Na primeira leitura, guarda o valor base
        if (passosIniciais == -1) {
            passosIniciais = total
        }

        // Passos da sessão = total atual - total inicial
        val passosAtual = total - passosIniciais

        onStepsChanged(passosAtual)
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {}
}