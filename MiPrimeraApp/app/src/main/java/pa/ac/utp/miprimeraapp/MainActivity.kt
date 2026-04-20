package pa.ac.utp.miprimeraapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val cardPeso = findViewById<MaterialCardView>(R.id.cardPeso)
        val cardPresion = findViewById<MaterialCardView>(R.id.cardPresion)
        val cardGlucosa = findViewById<MaterialCardView>(R.id.cardGlucosa)
        val cardActividad = findViewById<MaterialCardView>(R.id.cardActividad)
        val cardHidratacion = findViewById<MaterialCardView>(R.id.cardHidratacion)
        val cardMedicacion = findViewById<MaterialCardView>(R.id.cardMedicacion)

        cardPeso.setOnClickListener {
            makeText(this, "Accediendo a Peso IMC", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Peso::class.java)
            startActivity(intent)
        }

        cardPresion.setOnClickListener {
            makeText( this,"Accediendo a Presion Arterial",  Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Presion::class.java)
            startActivity(intent)
        }

        cardGlucosa.setOnClickListener {
            makeText( this, "Accediendo a Control de Glucosa", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Glucosa::class.java)
            startActivity(intent)
        }

        cardActividad.setOnClickListener {
            makeText(this,"Accediendo a Control de Actividad Fisica",  Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Actividad::class.java)
            startActivity(intent)
        }

        cardHidratacion.setOnClickListener {
            makeText( this, "Accediendo a Control de Hidratacion",  Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Hidratacion::class.java)
            startActivity(intent)
        }

        cardMedicacion.setOnClickListener {
            makeText(this, "Accediendo a Medicamentos",  Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Medicacion::class.java)
            startActivity(intent)
        }
    }
}