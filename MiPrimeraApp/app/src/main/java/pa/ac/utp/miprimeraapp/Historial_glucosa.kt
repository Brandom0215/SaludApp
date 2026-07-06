package pa.ac.utp.miprimeraapp

import android.content.Context
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Historial_glucosa : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_glucosa)

        dbHelper = DatabaseHelper(this)
        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)

        cargarHistorial()
    }

    private fun cargarHistorial() {
        val listaRegistrosDB = dbHelper.obtenerHistorialGlucosa(currentUserId)

        val lvHistorialGlucosa = findViewById<ListView>(R.id.lvHistorialGlucosa)
        val adapter = GlucosaAdapter(this, listaRegistrosDB) { position ->
            val registroId = listaRegistrosDB[position].id
            android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Deseas eliminar este registro de glucosa?")
                .setPositiveButton("Sí") { _, _ ->
                    dbHelper.eliminarGlucosa(registroId)
                    cargarHistorial()
                }
                .setNegativeButton("No", null)
                .show()
        }
        lvHistorialGlucosa.adapter = adapter
    }
}
