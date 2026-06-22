package pa.ac.utp.miprimeraapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class RegistroActividad(val nombre: String, val minutos: Int, val intensidad: String)

class Actividad : AppCompatActivity() {

    private val listaActividades = mutableListOf<RegistroActividad>()
    private lateinit var llActividadesList: LinearLayout

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val nombre = data.getStringExtra("nombre") ?: ""
                val minutos = data.getIntExtra("minutos", 0)
                val intensidad = data.getStringExtra("intensidad") ?: ""
                
                listaActividades.add(RegistroActividad(nombre, minutos, intensidad))
                actualizarLista()
                actualizarResumen()
            }
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

        llActividadesList = findViewById(R.id.llActividadesList)

        // Datos de prueba
        listaActividades.add(RegistroActividad("Caminar", 20, "Baja"))
        listaActividades.add(RegistroActividad("Gimnasio", 15, "Alta"))
        
        actualizarLista()
        actualizarResumen()

        val btnAgregarActividad = findViewById<Button>(R.id.btnAgregarActividad)
        btnAgregarActividad.setOnClickListener {
            val intent = Intent(this, AgregarActividad::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun actualizarLista() {
        llActividadesList.removeAllViews()
        for (i in listaActividades.indices) {
            val act = listaActividades[i]
            val view = layoutInflater.inflate(R.layout.item_actividad, llActividadesList, false)
            
            val tvNombre = view.findViewById<TextView>(R.id.tvNombreActividad)
            val tvDetalle = view.findViewById<TextView>(R.id.tvDetalleActividad)
            val ivDelete = view.findViewById<ImageView>(R.id.ivDeleteActividad)

            tvNombre.text = act.nombre
            tvDetalle.text = "${act.minutos} min - Intensidad ${act.intensidad}"

            ivDelete.setOnClickListener {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar ejercicio")
                    .setMessage("¿Deseas eliminar este registro de actividad?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        listaActividades.removeAt(i)
                        actualizarLista()
                        actualizarResumen()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            llActividadesList.addView(view)
        }
    }

    private fun actualizarResumen() {
        var totalMinutos = 0
        for (act in listaActividades) {
            totalMinutos += act.minutos
        }
        
        val totalKcal = totalMinutos * 10
        val totalPasos = totalMinutos * 110

        val tvPasos = findViewById<TextView>(R.id.tvPasos)
        val tvKcal = findViewById<TextView>(R.id.tvKcal)
        val tvMinutos = findViewById<TextView>(R.id.tvMinutos)
        val tvPorcentaje = findViewById<TextView>(R.id.tvPorcentaje)
        val pbMinutosCircle = findViewById<ProgressBar>(R.id.pbMinutosCircle)

        val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
        tvPasos.text = formatter.format(totalPasos)
        tvKcal.text = formatter.format(totalKcal)
        tvMinutos.text = formatter.format(totalMinutos)

        val meta = 60
        var porcentaje = (totalMinutos * 100) / meta
        if (porcentaje > 100) porcentaje = 100

        tvPorcentaje.text = "$porcentaje%"
        pbMinutosCircle.progress = porcentaje
    }
}