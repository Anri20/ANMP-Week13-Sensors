package com.example.week13

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.week13.databinding.ActivityMainBinding
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var sensorManager: SensorManager
    private var accelerometerReading = FloatArray(3)
    private var magneticReading = FloatArray(3)

    private var accelerometerSensor: Sensor? = null
    private var geomagneticSensor: Sensor? = null
    private var lightSensor: Sensor? = null
    private var proximitySensor: Sensor? = null

    private var previousV: Float? = null
    private var stepCount: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    override fun onResume() {
        super.onResume()

//        Accelerometer Sensor
        if (accelerometerSensor != null) {
            Toast.makeText(this, "Accelerometer Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(
                this,
                accelerometerSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        } else {
            Toast.makeText(this, "No Accelerometer Sensor detected", Toast.LENGTH_SHORT).show()
        }

//        Geomagnetic Sensor
        if (geomagneticSensor != null) {
            Toast.makeText(this, "Magnetic Field Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(
                this,
                geomagneticSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        } else {
            Toast.makeText(this, "No Magnetic Field Sensor detected", Toast.LENGTH_SHORT).show()
        }

//        Light Sensor
        if (lightSensor != null) {
            Toast.makeText(this, "Light Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(
                this,
                lightSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        } else {
            Toast.makeText(this, "No Light Sensor detected", Toast.LENGTH_SHORT).show()
        }

//        Proximity Sensor
        if (proximitySensor != null) {
            Toast.makeText(this, "Proximity Sensor detected", Toast.LENGTH_SHORT).show()
            sensorManager.registerListener(
                this,
                proximitySensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        } else {
            Toast.makeText(this, "No Proximity Sensor detected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    accelerometerReading = it.values
                    var x = it.values[0]
                    var y = it.values[1]
                    var z = it.values[2]
                    binding.txtAccelerometer.text = "x: $x, y: $y, z: $z"

                    var v = sqrt(x.pow(2) + y.pow(2) + z.pow(2))

                    previousV?.let {
                        var diff = v - it
                        if (diff > 6) {
                            stepCount++
                            binding.txtStep.text = "Steps: $stepCount"
                        }
                    }
                    previousV = v
                }

                Sensor.TYPE_MAGNETIC_FIELD -> {
                    magneticReading = it.values
                }

                Sensor.TYPE_LIGHT -> {
                    binding.txtLight.text = it.values[0].toString()
                }

                Sensor.TYPE_PROXIMITY -> {
                    binding.txtProximity.text = it.values[0].toString()
                    if (it.values[0] <= 0.0){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }
        }

        var rotationMatrix = FloatArray(9)
        var orientationAngles = FloatArray(3)

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magneticReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

//        Yaw and Azimuth is the same thing
        var yaw = Math.toDegrees(orientationAngles[0].toDouble())
        var pitch = Math.toDegrees(orientationAngles[1].toDouble())
        var roll = Math.toDegrees(orientationAngles[2].toDouble())

        binding.txtGyro.text =
            "Yaw = ${yaw.toInt()}\nPitch = ${pitch.toInt()}\nRoll = ${roll.toInt()}"
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { }
}