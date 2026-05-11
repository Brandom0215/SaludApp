package pa.ac.utp.miprimeraapp

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Historial_peso : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial_peso)

        // Lista de datos estáticos
        val listaRegistros = listOf(
            RegistroPeso("01/01/2024", 95.5, 42.4),
            RegistroPeso("16/01/2024", 93.8, 31.8),
            RegistroPeso("31/01/2024", 92.0, 31.2),
            RegistroPeso("15/02/2024", 90.5, 30.7),
            RegistroPeso("01/03/2024", 88.0, 29.8),
            RegistroPeso("16/03/2024", 86.2, 29.2),
            RegistroPeso("31/03/2024", 84.5, 28.6),
            RegistroPeso("15/04/2024", 82.0, 27.8),
            RegistroPeso("30/04/2024", 79.5, 26.9),
            RegistroPeso("15/05/2024", 77.0, 26.1),
            RegistroPeso("30/05/2024", 74.5, 25.2),
            RegistroPeso("14/06/2024", 72.0, 24.4),
            RegistroPeso("29/06/2024", 70.5, 23.9),
            RegistroPeso("14/07/2024", 69.0, 23.4),
            RegistroPeso("29/07/2024", 67.5, 22.9)
        )

        val lvHistorial = findViewById<ListView>(R.id.lvHistorial)
        val adapter = PesoAdapter(this, listaRegistros)
        lvHistorial.adapter = adapter
    }
}