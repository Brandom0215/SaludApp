package pa.ac.utp.miprimeraapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class Presion : AppCompatActivity() {

    // Vistas
    private lateinit var btnFecha: LinearLayout
    private lateinit var txtFecha: TextView
    private lateinit var txtHora: TextView
    private lateinit var tvSistolica: TextView
    private lateinit var tvDiastolica: TextView
    private lateinit var tvPulso: TextView
    private lateinit var rgBrazo: RadioGroup
    private lateinit var btnAnalizar: Button
    private lateinit var txtResultado: TextView
    private lateinit var txtClasificacion: TextView
    private lateinit var txtTip: TextView
    private lateinit var cardResultado: LinearLayout
    private lateinit var layoutTip: LinearLayout


    // Estado, los valores manuales
    private var fechaSeleccionada: String? = null
    private var valorSistolica = 120
    private var valorDiastolica = 80
    private var valorPulso = 72

    // Límites
    private val sistolicaMin = 80;  private val sistolicaMax = 200
    private val diastolicaMin = 40; private val diastolicaMax = 130
    private val pulsoMin = 40;      private val pulsoMax = 180

    // Handler y Runnable como campos de clase para que no se pierdan
    private val handler = Handler(Looper.getMainLooper())
    private val relojRunnable = object : Runnable {
        override fun run() {
            val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
            txtHora.text = formato.format(Date())
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_presion)

        initViews()
        actualizarValores()
        configurarFlechas()
        configurarEventos()
        iniciarReloj()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(relojRunnable)
    }


    // Inicializar vistas
    private fun initViews() {
        btnFecha = findViewById(R.id.btnFecha)
        txtFecha = findViewById(R.id.txtFecha)
        txtHora = findViewById(R.id.txtHora)

        tvSistolica = findViewById(R.id.tvSistolica)
        tvDiastolica = findViewById(R.id.tvDiastolica)
        tvPulso = findViewById(R.id.tvPulso)

        rgBrazo = findViewById(R.id.rgBrazo)
        btnAnalizar = findViewById(R.id.btnAnalizar)

        txtResultado = findViewById(R.id.txtResultado)
        txtClasificacion = findViewById(R.id.txtClasificacion)
        txtTip = findViewById(R.id.txtTip)
        cardResultado = findViewById(R.id.cardResultado)
        layoutTip = findViewById(R.id.layoutTip)
    }

    // Mostrar valores en círculos
    private fun actualizarValores() {
        tvSistolica.text = valorSistolica.toString()
        tvDiastolica.text = valorDiastolica.toString()
        tvPulso.text = valorPulso.toString()
    }


    // Flechas
    private fun configurarFlechas() {
        // Sistólica
        findViewById<TextView>(R.id.arrowUpSistolica).setOnClickListener {
            if (valorSistolica < sistolicaMax) {
                valorSistolica++
                tvSistolica.text = valorSistolica.toString()
            }
        }
        findViewById<TextView>(R.id.arrowDownSistolica).setOnClickListener {
            if (valorSistolica > sistolicaMin) {
                valorSistolica--
                tvSistolica.text = valorSistolica.toString()
            }
        }

        // Diastólica
        findViewById<TextView>(R.id.arrowUpDiastolica).setOnClickListener {
            if (valorDiastolica < diastolicaMax) {
                valorDiastolica++
                tvDiastolica.text = valorDiastolica.toString()
            }
        }
        findViewById<TextView>(R.id.arrowDownDiastolica).setOnClickListener {
            if (valorDiastolica > diastolicaMin) {
                valorDiastolica--
                tvDiastolica.text = valorDiastolica.toString()
            }
        }

        // Pulso
        findViewById<TextView>(R.id.arrowUpPulso).setOnClickListener {
            if (valorPulso < pulsoMax) {
                valorPulso++
                tvPulso.text = valorPulso.toString()
            }
        }
        findViewById<TextView>(R.id.arrowDownPulso).setOnClickListener {
            if (valorPulso > pulsoMin) {
                valorPulso--
                tvPulso.text = valorPulso.toString()
            }
        }
    }

    // Eventos
    private fun configurarEventos() {

        btnFecha.setOnClickListener {
            mostrarDatePicker()
        }

        btnAnalizar.setOnClickListener {
            analizarMedicion()
        }
    }

    // DatePicker
    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                fechaSeleccionada = String.format("%02d/%02d/%04d", day, month + 1, year)
                txtFecha.text = fechaSeleccionada
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    // Reloj en tiempo real
    private fun iniciarReloj() {
        // Actualizar inmediatamente al iniciar
        val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
        txtHora.text = formato.format(Date())

        // Programar actualizaciones cada segundo
        handler.post(relojRunnable)
    }

    // Lógica principal
    private fun analizarMedicion() {

        if (fechaSeleccionada == null) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        if (rgBrazo.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Selecciona el brazo", Toast.LENGTH_SHORT).show()
            return
        }

        val sistolica = valorSistolica
        val diastolica = valorDiastolica
        val pulso = valorPulso
        val hora = txtHora.text.toString()

        val brazo = findViewById<RadioButton>(
            rgBrazo.checkedRadioButtonId
        ).text.toString()

        val clasificacion = clasificarPresion(sistolica, diastolica)

        // Mostrar la card de resultados
        cardResultado.visibility = View.VISIBLE


        val brazoCorto = if (brazo.contains("Izquierdo", true)) "Izquierdo" else "Derecho"

        txtResultado.text = """
            Fecha: $fechaSeleccionada
            Hora: $hora
            Sistólica: $sistolica mmHg    Pulso: $pulso BPM
            Diastólica: $diastolica mmHg    Brazo: $brazoCorto
        """.trimIndent()

        actualizarBadge(clasificacion)
    }

    // Clasificación
    private fun clasificarPresion(s: Int, d: Int): String {
        return when {
            s < 90 || d < 60 -> "BAJA"
            s in 90..119 && d in 60..79 -> "NORMAL"
            s in 120..129 && d < 80 -> "ELEVADA"
            else -> "ALTA"
        }
    }

    // Badge dinámico
    private fun actualizarBadge(tipo: String) {

        txtClasificacion.text = tipo
        txtClasificacion.setTextColor(0xFFFFFFFF.toInt())

        when (tipo) {
            "NORMAL" -> {
                txtClasificacion.setBackgroundResource(R.drawable.presion_etiqueta_verde)
                txtTip.text = "Su presión está en un rango saludable.\nMantenga un estilo de vida activo."
            }
            "ELEVADA" -> {
                txtClasificacion.setBackgroundResource(R.drawable.presion_etiqueta_amarillo)
                txtClasificacion.setTextColor(0xFF333333.toInt())
                txtTip.text = "Su presión está ligeramente elevada.\nReduzca el consumo de sal y mantenga actividad física."
            }
            "ALTA" -> {
                txtClasificacion.setBackgroundResource(R.drawable.presion_etiqueta_rojo)
                txtTip.text = "Su presión es alta. Consulte a un médico\ny controle su alimentación y estrés."
            }
            else -> {
                txtClasificacion.setBackgroundResource(R.drawable.presion_etiqueta_gris)
                txtTip.text = "Su presión es baja. Manténgase hidratado\ny consulte a un profesional si presenta síntomas."
            }
        }

        // Tip
        txtTip.setTextColor(0xFF555555.toInt())
        layoutTip.setBackgroundResource(R.drawable.presion_consejo_info)
    }
}