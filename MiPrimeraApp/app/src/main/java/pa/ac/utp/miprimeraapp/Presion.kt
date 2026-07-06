package pa.ac.utp.miprimeraapp



import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

import android.content.Context

class Presion : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L

    // Vistas
    private lateinit var btnFecha: View
    private lateinit var btnHora: View
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
    private lateinit var cardResultado: View
    private lateinit var layoutTip: LinearLayout


    // Estado, los valores manuales
    private var fechaSeleccionada: String? = null
    private var horaSeleccionada: String? = null
    private var valorSistolica = 120
    private var valorDiastolica = 80
    private var valorPulso = 72

    // Límites
    private val sistolicaMin = 80;  private val sistolicaMax = 200
    private val diastolicaMin = 40; private val diastolicaMax = 130
    private val pulsoMin = 40;      private val pulsoMax = 180



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_presion)
        val vistaPrincipal = findViewById<View>(R.id.main)
        if (vistaPrincipal != null) {
            ViewCompat.setOnApplyWindowInsetsListener(vistaPrincipal) { v, insets ->
                val barrasSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(barrasSistema.left, barrasSistema.top, barrasSistema.right, barrasSistema.bottom)
                insets
            }
        }

        dbHelper = DatabaseHelper(this)
        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)

        initViews()
        actualizarValores()
        configurarFlechas()
        configurarEventos()
    }


    // Inicializar vistas
    private fun initViews() {
        btnFecha = findViewById(R.id.btnFecha)
        btnHora = findViewById(R.id.btnHora)
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

        btnHora.setOnClickListener {
            mostrarTimePicker()
        }

        btnAnalizar.setOnClickListener {
            analizarMedicion()
        }

        val btnVerHistorial = findViewById<Button>(R.id.btnVerHistorial)
        btnVerHistorial.setOnClickListener {
            val intent = android.content.Intent(this, Historial_presion::class.java)
            startActivity(intent)
        }
    }

    // DatePicker
    private fun mostrarDatePicker() {
        val datePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccionar Fecha")
            .setSelection(com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
            .build()
            
        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            
            fechaSeleccionada = String.format("%02d/%02d/%04d", day, month + 1, year)
            txtFecha.text = fechaSeleccionada
        }
        
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    // TimePicker
    private fun mostrarTimePicker() {
        val calendar = java.util.Calendar.getInstance()
        val timePicker = com.google.android.material.timepicker.MaterialTimePicker.Builder()
            .setTimeFormat(com.google.android.material.timepicker.TimeFormat.CLOCK_24H)
            .setHour(calendar.get(java.util.Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(java.util.Calendar.MINUTE))
            .setTitleText("Seleccionar Hora")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            horaSeleccionada = String.format("%02d:%02d", hour, minute)
            txtHora.text = horaSeleccionada
        }

        timePicker.show(supportFragmentManager, "TIME_PICKER")
    }

    // Lógica principal
    private fun analizarMedicion() {

        if (fechaSeleccionada == null) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        if (horaSeleccionada == null) {
            Toast.makeText(this, "Selecciona una hora", Toast.LENGTH_SHORT).show()
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

        if (currentUserId != -1L) {
            dbHelper.registrarPresion(currentUserId, sistolica, diastolica, pulso)
            Toast.makeText(this, "Registro guardado", Toast.LENGTH_SHORT).show()
        }
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
