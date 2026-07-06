package pa.ac.utp.miprimeraapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import androidx.appcompat.widget.SwitchCompat
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
// data class RegistroMedicamento is removed, using RegistroMedicamentoDB
class Medicacion : AppCompatActivity() {
    private val listaMedicamentos = mutableListOf<RegistroMedicamentoDB>()
    private lateinit var adapter: MedicamentoAdapter
    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L
    private lateinit var prefs: SharedPreferences
    private var recordatoriosActivos: Boolean = true

    private fun ordenarLista() {
        // Ordenar por hora en lugar de por estado "tomado" para evitar que los items salten de posición
        listaMedicamentos.sortBy { it.hora }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val isEdit = data.getBooleanExtra("isEdit", false)
                val nombre = data.getStringExtra("nombre") ?: ""
                val hora = data.getStringExtra("hora") ?: ""
                val dosis = data.getStringExtra("dosis") ?: ""
                
                if (isEdit) {
                    val pos = data.getIntExtra("position", -1)
                    if (pos != -1) {
                        val med = listaMedicamentos[pos]
                        dbHelper.actualizarMedicamento(med.id, nombre, hora, dosis)
                        if (recordatoriosActivos) programarAlarma(med.id, nombre, hora)
                    }
                } else {
                    if (currentUserId != -1L) {
                        val id = dbHelper.registrarMedicamento(currentUserId, nombre, hora, dosis)
                        if (recordatoriosActivos) programarAlarma(id, nombre, hora)
                    }
                }
                cargarMedicamentos()
            }
        }
    }

    private fun programarAlarma(id: Long, nombre: String, hora: String) {
        try {
            val parts = hora.split(" ")
            val timeParts = parts[0].split(":")
            var h = timeParts[0].toInt()
            val m = timeParts[1].toInt()
            val isPM = parts.size > 1 && parts[1] == "PM"
            val isAM = parts.size > 1 && parts[1] == "AM"

            if (isPM && h != 12) h += 12
            if (isAM && h == 12) h = 0

            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, h)
            calendar.set(java.util.Calendar.MINUTE, m)
            calendar.set(java.util.Calendar.SECOND, 0)
            
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }

            val alarmManager = getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
            val intent = Intent(this, pa.ac.utp.miprimeraapp.services.MedicamentoReceiver::class.java).apply {
                putExtra("nombre_medicamento", nombre)
            }
            
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                this, 
                id.toInt(), 
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            // Usamos setRepeating para alarmas diarias. En versiones recientes puede no ser exacto al milisegundo, pero es suficiente.
            alarmManager.setRepeating(
                android.app.AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                android.app.AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelarAlarma(id: Long) {
        val alarmManager = getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(this, pa.ac.utp.miprimeraapp.services.MedicamentoReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            this, 
            id.toInt(), 
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun cargarMedicamentos() {
        if (currentUserId != -1L) {
            listaMedicamentos.clear()
            val medicamentosDb = dbHelper.obtenerMedicamentos(currentUserId)
            listaMedicamentos.addAll(medicamentosDb)
            
            // Reprogramar alarmas si están activas
            for (med in medicamentosDb) {
                if (recordatoriosActivos && !med.tomado) {
                    programarAlarma(med.id, med.nombre, med.hora)
                } else {
                    cancelarAlarma(med.id)
                }
            }
            
            ordenarLista()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Solicitar permiso de notificaciones para Android 13+ si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            finish()
        }

        dbHelper = DatabaseHelper(this)
        prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)
        
        recordatoriosActivos = prefs.getBoolean("medicamentos_recordatorios", true)

        val switchRecordatorios = findViewById<SwitchCompat>(R.id.switchRecordatorios)
        
        switchRecordatorios.setOnCheckedChangeListener(null)
        switchRecordatorios.isChecked = recordatoriosActivos
        switchRecordatorios.setOnCheckedChangeListener { _, isChecked ->
            recordatoriosActivos = isChecked
            prefs.edit().putBoolean("medicamentos_recordatorios", isChecked).commit()
            
            if (isChecked) {
                for (med in listaMedicamentos) {
                    if (!med.tomado) programarAlarma(med.id, med.nombre, med.hora)
                }
            } else {
                for (med in listaMedicamentos) {
                    cancelarAlarma(med.id)
                }
            }
            cargarMedicamentos()
        }

        val lvMedicamentos = findViewById<ListView>(R.id.lvMedicamentos)
        adapter = MedicamentoAdapter(this, listaMedicamentos) { position, action ->
            if (action == "DELETE") {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar medicamento")
                    .setMessage("¿Estás seguro de que deseas eliminar este medicamento de tu rutina?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        val med = listaMedicamentos[position]
                        dbHelper.eliminarMedicamento(med.id)
                        cancelarAlarma(med.id)
                        cargarMedicamentos()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else if (action == "EDIT") {
                val med = listaMedicamentos[position]
                val intent = Intent(this, AgregarMedicamento::class.java)
                intent.putExtra("isEdit", true)
                intent.putExtra("position", position)
                intent.putExtra("nombre", med.nombre)
                intent.putExtra("horaStr", med.hora)
                intent.putExtra("dosisStr", med.dosis)
                resultLauncher.launch(intent)
            } else if (action == "TOGGLE") {
                val med = listaMedicamentos[position]
                dbHelper.marcarMedicamentoTomado(currentUserId, med.id, med.tomado)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    cargarMedicamentos()
                }, 400)
            }
        }
        lvMedicamentos.adapter = adapter

        // Cargar desde DB (después de inicializar el adapter)
        cargarMedicamentos()

        val btnAgregarMedicamento = findViewById<Button>(R.id.btnAgregarMedicamento)
        btnAgregarMedicamento.setOnClickListener {
            val intent = Intent(this, AgregarMedicamento::class.java)
            resultLauncher.launch(intent)
        }
    }
}