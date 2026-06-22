package pa.ac.utp.miprimeraapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class AgregarMedicamento : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_medicamento)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            finish()
        }

        val etNombre = findViewById<EditText>(R.id.etNombreMed)
        val timePicker = findViewById<TimePicker>(R.id.timePickerHora)
        val numberPicker = findViewById<NumberPicker>(R.id.numberPickerDosis)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarMedicamento)

        numberPicker.minValue = 1
        numberPicker.maxValue = 10
        numberPicker.wrapSelectorWheel = false

        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            btnGuardar.text = "Actualizar"
            etNombre.setText(intent.getStringExtra("nombre") ?: "")
            
            val horaStr = intent.getStringExtra("horaStr") ?: ""
            if (horaStr.isNotEmpty()) {
                try {
                    val parts = horaStr.split(" ")
                    val timeParts = parts[0].split(":")
                    var h = timeParts[0].toInt()
                    val m = timeParts[1].toInt()
                    if (parts.size > 1) {
                        if (parts[1] == "PM" && h != 12) h += 12
                        if (parts[1] == "AM" && h == 12) h = 0
                    }
                    timePicker.hour = h
                    timePicker.minute = m
                } catch(e: Exception) {}
            }

            val dosisStr = intent.getStringExtra("dosisStr") ?: ""
            if (dosisStr.isNotEmpty()) {
                try {
                    val num = dosisStr.split(" ")[0].toInt()
                    numberPicker.value = num
                } catch(e: Exception) {}
            }
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa un nombre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hour = timePicker.hour
            val minute = timePicker.minute
            
            val amPm = if (hour < 12) "AM" else "PM"
            val hourFormatted = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            val minuteStr = String.format(Locale.getDefault(), "%02d", minute)
            val horaGuardada = "$hourFormatted:$minuteStr $amPm"
            
            val dosisCantidad = numberPicker.value
            val textoDosis = if (dosisCantidad == 1) "1 Pastilla" else "$dosisCantidad Pastillas"

            val intentResult = Intent()
            intentResult.putExtra("nombre", nombre)
            intentResult.putExtra("hora", horaGuardada)
            intentResult.putExtra("dosis", textoDosis)
            intentResult.putExtra("isEdit", isEdit)
            if (isEdit) {
                intentResult.putExtra("position", intent.getIntExtra("position", -1))
            }
            setResult(Activity.RESULT_OK, intentResult)
            finish()
        }
    }
}
