package com.example.sprinttrack.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounterManager(
    context: Context,
    private val onStepsChanged: (Int) -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager

    private val stepSensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        )

    private var passosIniciais = -1

    fun start() {

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

        val total =
            event?.values?.get(0)?.toInt() ?: 0

        if (passosIniciais == -1) {
            passosIniciais = total
        }

        val passosAtual =
            total - passosIniciais

        onStepsChanged(passosAtual)
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {}
}