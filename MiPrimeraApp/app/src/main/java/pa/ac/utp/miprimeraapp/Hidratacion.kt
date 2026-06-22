package pa.ac.utp.miprimeraapp

import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Hidratacion : AppCompatActivity() {

    private var vasosConsumidos = 0
    private val metaVasos = 8 // Objetivo diario de 8 vasos

    private lateinit var tvConsumoVasos: TextView
    private lateinit var ivCuerpoLleno: ImageView
    private lateinit var drawableRecorte: ClipDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hidratacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvConsumoVasos = findViewById(R.id.tvConsumoVasos)
        ivCuerpoLleno = findViewById(R.id.ivCuerpoLleno)
        
        drawableRecorte = ivCuerpoLleno.drawable as ClipDrawable

        val btn1Vaso = findViewById<Button>(R.id.btn1Vaso)
        val btn2Vasos = findViewById<Button>(R.id.btn2Vasos)
        val btnVerHistorial = findViewById<Button>(R.id.btnVerHistorial)
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)

        actualizarUI()

        btn1Vaso.setOnClickListener {
            agregarAgua(1)
        }

        btn2Vasos.setOnClickListener {
            agregarAgua(2)
        }

        btnVerHistorial.setOnClickListener {
            val intent = android.content.Intent(this, Historial_hidratacion::class.java)
            startActivity(intent)
        }
        
        btnRegresar.setOnClickListener {
            finish()
        }
    }

    private fun agregarAgua(cantidadVasos: Int) {
        vasosConsumidos += cantidadVasos
        if (vasosConsumidos > metaVasos * 2) {
            vasosConsumidos = metaVasos * 2
        }
        actualizarUI()
    }

    private fun actualizarUI() {
        tvConsumoVasos.text = "Hoy has bebido: $vasosConsumidos Vasos"
        
        var porcentaje = (vasosConsumidos.toFloat() / metaVasos)
        if (porcentaje > 1f) porcentaje = 1f
        val nivel = (porcentaje * 10000).toInt()
        
        drawableRecorte.level = nivel
    }
}