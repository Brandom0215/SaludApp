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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_hidratacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            finish()
        }

        val listaRegistros = listOf(
            RegistroHidratacion("Hoy", 8, 2000, true),
            RegistroHidratacion("Ayer", 6, 1500, false),
            RegistroHidratacion("19/06/2024", 8, 2000, true),
            RegistroHidratacion("18/06/2024", 5, 1250, false),
            RegistroHidratacion("17/06/2024", 10, 2500, true),
            RegistroHidratacion("16/06/2024", 7, 1750, false),
            RegistroHidratacion("15/06/2024", 8, 2000, true)
        )

        val lvHistorialHidratacion = findViewById<ListView>(R.id.lvHistorialHidratacion)
        val adapter = HidratacionAdapter(this, listaRegistros)
        lvHistorialHidratacion.adapter = adapter
    }
}
