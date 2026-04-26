package pa.ac.utp.miprimeraapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class Peso : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_peso)

        val etEdad = findViewById<EditText>(R.id.etEdad)
        val etPeso = findViewById<EditText>(R.id.etPeso)
        val etEstatura = findViewById<EditText>(R.id.etEstatura)
        val swPeso = findViewById<SwitchCompat>(R.id.swPesoUnit)
        val swEstatura = findViewById<SwitchCompat>(R.id.swEstaturaUnit)
        val btnCalcular = findViewById<Button>(R.id.btnCalcular)
        val tvIMC = findViewById<TextView>(R.id.tvIMC)
        val tvPesoIdeal = findViewById<TextView>(R.id.tvPesoIdeal)
        val tvGrasa = findViewById<TextView>(R.id.tvGrasa)
        val tvClasificacion = findViewById<TextView>(R.id.tvClasificacion)

        // Listeners para cambiar los Hints dinámicamente
        swPeso.setOnCheckedChangeListener { _, isChecked ->
            etPeso.hint = if (isChecked) "Peso (Lb)" else "Peso (Kg)"
            etPeso.text.clear()
        }
        swEstatura.setOnCheckedChangeListener { _, isChecked ->
            etEstatura.hint = if (isChecked) "Estatura (in)" else "Estatura (cm)"
            etEstatura.text.clear()
        }

        fun categorizarIMC(imc: Double): String {
            return when {
                imc < 18.5 -> "Bajo Peso"
                imc < 25 -> "Normal"
                imc < 30 -> "Sobrepeso"
                imc < 35 -> "Obesidad I"
                imc < 40 -> "Obesidad II"
                else -> "Obesidad III"
            }
        }

        btnCalcular.setOnClickListener {

            // 1. Obtener valores
            val sEdad = etEdad.text.toString()
            val sPeso = etPeso.text.toString()
            val sEstatura = etEstatura.text.toString()

            // 2. Validar campos vacíos
            if (sEdad.isEmpty() || sPeso.isEmpty() || sEstatura.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Convertir a números
            val edad = sEdad.toInt()
            var peso = sPeso.toDouble()
            var estatura = sEstatura.toDouble()

            // 4. Normalizar unidades
            if (swPeso.isChecked) {
                peso *= 0.453592   // lb → kg
            }

            if (swEstatura.isChecked) {
                estatura *= 2.54   // in → cm
            }

            // 5. Convertir a metros
            val estaturaMetros = estatura / 100

            // 6. Calcular valores
            val imc = peso / (estaturaMetros * estaturaMetros)
            val pesoIdeal = 22 * (estaturaMetros * estaturaMetros)
            val grasa = (1.20 * imc) + (0.23 * edad) - 16.2

            // 7. Mostrar resultados
            tvIMC.text = String.format("%.1f", imc)
            tvPesoIdeal.text = String.format("%.1f kg", pesoIdeal)
            tvGrasa.text = String.format("%.1f%%", grasa)

            // 8. Clasificación
            tvClasificacion.text = categorizarIMC(imc)
        }




















    }
}