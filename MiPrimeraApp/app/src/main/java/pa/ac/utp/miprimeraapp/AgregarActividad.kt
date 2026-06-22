package pa.ac.utp.miprimeraapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarActividad : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_actividad)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            finish()
        }

        val etNombre = findViewById<EditText>(R.id.etNombreActividad)
        val npMinutos = findViewById<NumberPicker>(R.id.npMinutos)
        val spinnerIntensidad = findViewById<Spinner>(R.id.spinnerIntensidad)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarActividad)

        // Configurar NumberPicker
        npMinutos.minValue = 1
        npMinutos.maxValue = 180
        npMinutos.value = 30 // Valor por defecto
        npMinutos.wrapSelectorWheel = false

        // Configurar Spinner
        val opcionesIntensidad = arrayOf("Baja", "Media", "Alta")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcionesIntensidad)
        spinnerIntensidad.adapter = adapter
        spinnerIntensidad.setSelection(1) // Seleccionar "Media" por defecto

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa un nombre para el ejercicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val minutos = npMinutos.value
            val intensidad = spinnerIntensidad.selectedItem.toString()

            val intentResult = Intent()
            intentResult.putExtra("nombre", nombre)
            intentResult.putExtra("minutos", minutos)
            intentResult.putExtra("intensidad", intensidad)
            setResult(Activity.RESULT_OK, intentResult)
            finish()
        }
    }
}
