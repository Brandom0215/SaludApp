package pa.ac.utp.miprimeraapp

import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Context
import android.widget.Toast
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.SwitchCompat
import pa.ac.utp.miprimeraapp.services.HidratacionReceiver

class Hidratacion : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L

    private var mlConsumidos = 0
    private var metaMl = 2000 // Objetivo diario por defecto 2000ml

    private lateinit var tvConsumoMl: TextView
    private lateinit var ivCuerpoLleno: ImageView
    private lateinit var drawableRecorte: ClipDrawable
    private lateinit var switchNotificaciones: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hidratacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)

        if (currentUserId != -1L) {
            val peso = dbHelper.obtenerUltimoPeso(currentUserId)
            if (peso != null) {
                metaMl = (peso * 35).toInt()
            }
            
            mlConsumidos = dbHelper.obtenerMlHoy(currentUserId)
            if (mlConsumidos > metaMl * 2) mlConsumidos = metaMl * 2
        }

        tvConsumoMl = findViewById(R.id.tvConsumoMl)
        ivCuerpoLleno = findViewById(R.id.ivCuerpoLleno)
        switchNotificaciones = findViewById(R.id.switchNotificaciones)
        
        drawableRecorte = ivCuerpoLleno.drawable as ClipDrawable

        val btnRestar250 = findViewById<Button>(R.id.btnRestar250)
        val btnSumar250 = findViewById<Button>(R.id.btnSumar250)
        val btnSumar500 = findViewById<Button>(R.id.btnSumar500)
        val btnVerHistorial = findViewById<Button>(R.id.btnVerHistorial)
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)

        actualizarUI()

        btnRestar250.setOnClickListener {
            agregarAgua(-250)
        }

        btnSumar250.setOnClickListener {
            agregarAgua(250)
        }

        btnSumar500.setOnClickListener {
            agregarAgua(500)
        }

        configurarNotificaciones()

        btnVerHistorial.setOnClickListener {
            val intent = android.content.Intent(this, Historial_hidratacion::class.java)
            startActivity(intent)
        }
        
        btnRegresar.setOnClickListener {
            finish()
        }
    }

    private fun configurarNotificaciones() {
        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        val recordatoriosActivos = prefs.getBoolean("notificaciones_agua", false)
        
        switchNotificaciones.setOnCheckedChangeListener(null)
        switchNotificaciones.isChecked = recordatoriosActivos

        switchNotificaciones.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) { // Solo responder a interacciones reales del usuario
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
                        } else {
                            programarAlarma()
                            prefs.edit().putBoolean("notificaciones_agua", true).commit()
                        }
                    } else {
                        programarAlarma()
                        prefs.edit().putBoolean("notificaciones_agua", true).commit()
                    }
                } else {
                    cancelarAlarma()
                    prefs.edit().putBoolean("notificaciones_agua", false).commit()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                programarAlarma()
                getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE).edit().putBoolean("notificaciones_agua", true).commit()
            } else {
                switchNotificaciones.setOnCheckedChangeListener(null)
                switchNotificaciones.isChecked = false
                configurarNotificaciones() // Restaurar listener
                Toast.makeText(this, "Permiso denegado para notificaciones", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun programarAlarma() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, HidratacionReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 100, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val intervalo = 2 * 60 * 60 * 1000L // 2 horas
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalo,
            intervalo,
            pendingIntent
        )
        Toast.makeText(this, "Recordatorios activados cada 2 horas", Toast.LENGTH_SHORT).show()
    }

    private fun cancelarAlarma() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, HidratacionReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 100, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Recordatorios desactivados", Toast.LENGTH_SHORT).show()
    }

    private fun agregarAgua(ml: Int) {
        if (currentUserId != -1L) {
            dbHelper.registrarHidratacion(currentUserId, ml)
            mlConsumidos += ml
            if (mlConsumidos < 0) mlConsumidos = 0
            if (mlConsumidos > metaMl * 2) {
                mlConsumidos = metaMl * 2
            }
            actualizarUI()
        } else {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarUI() {
        tvConsumoMl.text = "Hoy has bebido: $mlConsumidos / $metaMl ml"
        
        var porcentaje = (mlConsumidos.toFloat() / metaMl)
        if (porcentaje > 1f) porcentaje = 1f
        val nivel = (porcentaje * 10000).toInt()
        
        drawableRecorte.level = nivel
    }
}