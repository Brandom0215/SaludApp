package pa.ac.utp.miprimeraapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class RegistroHidratacion(val fecha: String, val vasos: Int, val mililitros: Int, val metaCumplida: Boolean)

class Historial_hidratacion : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_hidratacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        val prefs = getSharedPreferences("SaludAppPrefs", android.content.Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)

        cargarHistorial()
    }

    private fun cargarHistorial() {
        val peso = dbHelper.obtenerUltimoPeso(currentUserId)
        val metaMl = if (peso != null) (peso * 35).toInt() else 2000

        val listaRegistrosDB = dbHelper.obtenerHistorialHidratacion(currentUserId)

        val lvHistorialHidratacion = findViewById<ListView>(R.id.lvHistorialHidratacion)
        val adapter = HidratacionAdapter(this, listaRegistrosDB, metaMl) { position ->
            val registroId = listaRegistrosDB[position].id
            android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Deseas eliminar este registro de hidratación?")
                .setPositiveButton("Sí") { _, _ ->
                    dbHelper.eliminarHidratacion(registroId)
                    cargarHistorial()
                }
                .setNegativeButton("No", null)
                .show()
        }
        lvHistorialHidratacion.adapter = adapter
    }
}
