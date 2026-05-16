package com.example.sprinttrack.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class MotionSensorManager(
    context: Context,
    private val onMovementDetected: () -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager

    private val accelerometer =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

    fun start() {
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val x = event?.values?.get(0) ?: 0f
        val y = event?.values?.get(1) ?: 0f
        val z = event?.values?.get(2) ?: 0f

        val movement = sqrt(
            (x*x + y*y + z*z).toDouble()
        )

        // Detecta movimento forte
        if (movement > 18) {
            onMovementDetected()
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {}
}