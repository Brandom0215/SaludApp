package pa.ac.utp.miprimeraapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pa.ac.utp.miprimeraapp.Actividad
import pa.ac.utp.miprimeraapp.DatabaseHelper
import pa.ac.utp.miprimeraapp.R
import java.text.SimpleDateFormat
import java.util.*

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var dbHelper: DatabaseHelper
    
    private var currentUserId: Long = -1L
    private var isFirstStep = true
    private var initialSteps = 0f
    private var stepsToday = 0
    private var currentDate: String = ""

    companion object {
        const val ACTION_STEP_UPDATE = "pa.ac.utp.miprimeraapp.STEP_UPDATE"
        const val EXTRA_STEPS = "steps"
        const val EXTRA_CALORIES = "calories"
        const val EXTRA_TIME = "time"
        private const val NOTIFICATION_CHANNEL_ID = "StepCounterChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        dbHelper = DatabaseHelper(this)
        
        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)
        
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newDate = sdf.format(Date())
        
        if (currentDate != newDate) {
            currentDate = newDate
            isFirstStep = true // Reset day
        }
        
        // Cargar pasos previos de hoy
        if (currentUserId != -1L) {
            stepsToday = dbHelper.obtenerPasos(currentUserId, currentDate)
        }
        
        startForeground(NOTIFICATION_ID, buildNotification(stepsToday))
        
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsFromReboot = event.values[0]
            
            // Check day change
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.format(Date())
            if (currentDate != today) {
                currentDate = today
                stepsToday = 0
                isFirstStep = true
            }
            
            if (isFirstStep) {
                initialSteps = totalStepsFromReboot
                isFirstStep = false
            } else {
                val newSteps = (totalStepsFromReboot - initialSteps).toInt()
                if (newSteps > 0) {
                    stepsToday += newSteps
                    initialSteps = totalStepsFromReboot
                    
                    if (currentUserId != -1L) {
                        dbHelper.guardarPasos(currentUserId, currentDate, stepsToday)
                    }
                    
                    val calorias = calcularCalorias(stepsToday)
                    
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, buildNotification(stepsToday))
                    
                    sendUpdateBroadcast(stepsToday, calorias)
                }
            }
        }
    }
    
    private fun calcularCalorias(pasos: Int): Double {
        if (currentUserId == -1L) return pasos * 0.04
        val peso = dbHelper.obtenerUltimoPeso(currentUserId)
        if (peso == null || peso == 0.0) {
            return pasos * 0.04
        }
        // Calorías = pasos * peso_kg * 0.000628
        return pasos * peso * 0.000628
    }
    
    private fun sendUpdateBroadcast(steps: Int, calories: Double) {
        val intent = Intent(ACTION_STEP_UPDATE)
        intent.putExtra(EXTRA_STEPS, steps)
        intent.putExtra(EXTRA_CALORIES, calories)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Contador de Pasos",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantiene el contador de pasos activo en segundo plano"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(steps: Int): Notification {
        val intent = Intent(this, Actividad::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("SaludApp - Actividad")
            .setContentText("Pasos hoy: $steps")
            .setSmallIcon(R.drawable.ic_actividad) // Valid icon needed
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
