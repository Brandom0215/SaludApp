package pa.ac.utp.miprimeraapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.graphics.Color
import pa.ac.utp.miprimeraapp.services.StepCounterService
import java.text.SimpleDateFormat
import java.util.*

class Actividad : AppCompatActivity() {

    private lateinit var tvPasos: TextView
    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L

    // Broadcast receiver para escuchar los pasos del servicio
    private val stepUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == StepCounterService.ACTION_STEP_UPDATE) {
                val pasosTotalesHoy = intent.getIntExtra(StepCounterService.EXTRA_STEPS, 0)
                val caloriasPasos = intent.getDoubleExtra(StepCounterService.EXTRA_CALORIES, 0.0)
                
                val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
                tvPasos.text = formatter.format(pasosTotalesHoy)
                
                // Actualizar resumen para reflejar el cambio de calorías y minutos
                actualizarResumen(caloriasPasos, pasosTotalesHoy)
            }
        }
    }
    
    private var lastCaloriasPasos = 0.0

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var granted = true
        permissions.entries.forEach {
            if (!it.value) granted = false
        }
        if (granted) {
            iniciarServicio()
        } else {
            Toast.makeText(this, "Permisos denegados, el contador en segundo plano no funcionará", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividad)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            finish()
        }

        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)
        dbHelper = DatabaseHelper(this)

        tvPasos = findViewById(R.id.tvPasos)
        
        requestPermissionsIfNeeded()
        cargarActividadesDesdeBD()

        val btnVerHistorial = findViewById<Button>(R.id.btnVerHistorial)
        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, Historial_actividad::class.java)
            startActivity(intent)
        }
        
        configurarGrafica()
    }

    private fun cargarActividadesDesdeBD() {
        if (currentUserId != -1L) {
            // Cargar pasos actuales directamente si el servicio no ha emitido aún
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val hoy = sdf.format(Date())
            val pasosHoy = dbHelper.obtenerPasos(currentUserId, hoy)
            tvPasos.text = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(pasosHoy)
            
            val peso = dbHelper.obtenerUltimoPeso(currentUserId) ?: 70.0
            lastCaloriasPasos = pasosHoy * peso * 0.000628
            
            actualizarResumen(lastCaloriasPasos, pasosHoy)
            actualizarGrafica()
        }
    }

    private fun actualizarResumen(caloriasPasos: Double = lastCaloriasPasos, pasosHoy: Int = 0) {
        lastCaloriasPasos = caloriasPasos
        // 100 pasos = aprox 1 minuto de actividad moderada
        val totalMinutos = (pasosHoy / 100)
        val totalKcal = caloriasPasos

        val tvKcal = findViewById<TextView>(R.id.tvKcal)
        val tvMinutos = findViewById<TextView>(R.id.tvMinutos)
        val tvPorcentaje = findViewById<TextView>(R.id.tvPorcentaje)
        val pbMinutosCircle = findViewById<ProgressBar>(R.id.pbMinutosCircle)

        val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
        tvKcal.text = formatter.format(totalKcal.toInt())
        tvMinutos.text = formatter.format(totalMinutos)

        // Meta diaria basada en calorías quemadas (ej. 500 kcal meta)
        val meta = 500.0
        var porcentaje = ((totalKcal * 100) / meta).toInt()
        if (porcentaje > 100) porcentaje = 100

        tvPorcentaje.text = "$porcentaje%"
        pbMinutosCircle.progress = porcentaje
    }

    private fun requestPermissionsIfNeeded() {
        val permissionsToRequest = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            iniciarServicio()
        }
    }

    private fun iniciarServicio() {
        val intent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            stepUpdateReceiver,
            IntentFilter(StepCounterService.ACTION_STEP_UPDATE)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepUpdateReceiver)
    }

    private fun configurarGrafica() {
        val barChart = findViewById<BarChart>(R.id.barChart)
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK
        
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        leftAxis.textColor = Color.BLACK
        
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
    }

    private fun actualizarGrafica() {
        val barChart = findViewById<BarChart>(R.id.barChart)
        val historialPasos = dbHelper.obtenerHistorialPasos(currentUserId, 7)
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        
        val displaySdf = SimpleDateFormat("E", Locale.getDefault())
        val peso = dbHelper.obtenerUltimoPeso(currentUserId) ?: 70.0
        
        for (i in 0..6) {
            val dateStr = sdf.format(cal.time)
            labels.add(displaySdf.format(cal.time).take(1).uppercase())
            
            val pasos = historialPasos[dateStr] ?: 0
            val caloriasPasos = pasos * peso * 0.000628
            
            val totalDia = caloriasPasos.toFloat()
            entries.add(BarEntry(i.toFloat(), totalDia))
            
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val dataSet = BarDataSet(entries, "Kcal")
        dataSet.color = Color.parseColor("#1A4375")
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.invalidate()
    }
}