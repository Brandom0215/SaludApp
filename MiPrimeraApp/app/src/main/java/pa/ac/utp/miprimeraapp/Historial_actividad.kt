package pa.ac.utp.miprimeraapp

import android.content.Context
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Historial_actividad : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_actividad)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)
        dbHelper = DatabaseHelper(this)

        cargarHistorial()
    }

    private fun cargarHistorial() {
        if (currentUserId != -1L) {
            val historial = dbHelper.obtenerHistorialPasos(currentUserId, 30) // Últimos 30 días
            
            val listaRegistros = mutableListOf<RegistroActividadList>()
            
            // Recorremos el historial y lo agregamos
            for ((fecha, pasos) in historial) {
                val peso = dbHelper.obtenerUltimoPeso(currentUserId) ?: 70.0
                val caloriasPasos = (pasos * peso * 0.000628).toInt()
                val minutos = pasos / 100 // Estimación de minutos activos
                
                listaRegistros.add(RegistroActividadList(fecha, pasos, caloriasPasos, minutos))
            }
            
            // Si no hay, agregamos uno dummy o vacío para mostrar algo o simplemente dejamos la lista vacía
            // pero el listView no muestra nada si está vacío, podemos poner un TextView "empty" pero por ahora lo dejamos.
            
            val adapter = ActividadAdapter(this, listaRegistros) { position ->
                val registroFecha = listaRegistros[position].fecha
                android.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar registro")
                    .setMessage("¿Deseas eliminar el registro de pasos de esta fecha?")
                    .setPositiveButton("Sí") { _, _ ->
                        dbHelper.eliminarPasos(currentUserId, registroFecha)
                        cargarHistorial()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
            val lvHistorial = findViewById<ListView>(R.id.lvHistorial)
            lvHistorial.adapter = adapter
        }
    }
}
