package pa.ac.utp.miprimeraapp

import android.content.Context
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Historial_peso : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_peso)

        dbHelper = DatabaseHelper(this)
        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)

        cargarHistorial()
    }

    private fun cargarHistorial() {
        val listaRegistrosDB = dbHelper.obtenerHistorialPesos(currentUserId)
        
        // Convertir de RegistroPesoDB a RegistroPeso para que el Adapter lo acepte
        val listaRegistros = listaRegistrosDB.map { 
            RegistroPeso(it.fecha.split(" ")[0], it.peso, it.imc) 
        }

        val lvHistorial = findViewById<ListView>(R.id.lvHistorial)
        val adapter = PesoAdapter(this, listaRegistros) { position ->
            val registroId = listaRegistrosDB[position].id
            android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Deseas eliminar este registro de peso?")
                .setPositiveButton("Sí") { _, _ ->
                    dbHelper.eliminarPeso(registroId)
                    cargarHistorial()
                }
                .setNegativeButton("No", null)
                .show()
        }
        lvHistorial.adapter = adapter
    }
}