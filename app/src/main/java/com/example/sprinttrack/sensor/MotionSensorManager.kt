package com.example.sprinttrack.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Gerencia o acelerômetro do dispositivo.
 * Detecta movimentos bruscos usando a magnitude do vetor de aceleração.
 * O acelerômetro mede aceleração nos 3 eixos.
 * Calculamos a magnitude do vetor resultante —
 * quanto maior o valor, mais intenso o movimento.
 * Acima de 18 consideramos que o usuário está correndo.
 */
class MotionSensorManager(
    context: Context,
    private val onMovementDetected: () -> Unit // Callback quando detecta movimento forte
) : SensorEventListener {

    // Acessa o serviço de sensores do sistema
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager

    // Obtém o sensor de acelerômetro padrão do dispositivo
    private val accelerometer =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

    /**
     * Inicia a escuta do acelerômetro.
     * SENSOR_DELAY_NORMAL: taxa de atualização padrão (cerca de 5Hz)
     */
    fun start() {
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    /**
     * Para a escuta do sensor, liberando recursos da bateria.
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    /**
     * Chamado automaticamente toda vez que o sensor detecta mudança.
     * Calcula a magnitude do vetor de aceleração (x² + y² + z²).
     */
    override fun onSensorChanged(event: SensorEvent?) {

        val x = event?.values?.get(0) ?: 0f // Eixo X (lateral)
        val y = event?.values?.get(1) ?: 0f // Eixo Y (vertical)
        val z = event?.values?.get(2) ?: 0f // Eixo Z (frente/trás)

        // Magnitude: raiz quadrada da soma dos quadrados
        val movement = sqrt(
            (x*x + y*y + z*z).toDouble()
        )

        // Detecta movimento forte
        // Valor 18 indica corrida intensa
        if (movement > 18) {
            onMovementDetected()
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {}
}