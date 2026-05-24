package pa.ac.utp.miprimeraapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class Glucosa : AppCompatActivity() {

    // Vistas
    private lateinit var tvFechaRegistro: TextView
    private lateinit var etGlucosaValor: EditText
    private lateinit var rgTipoRegistro: RadioGroup
    private lateinit var layoutCabeceraNotas: View
    private lateinit var layoutCuerpoNotas: View
    private lateinit var tvFlechaNotas: TextView
    private lateinit var etNotas: EditText
    private lateinit var btnGuardarRegistro: Button

    // Estado del panel colapsable
    private var notasExpandidas = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_glucosa)

        // Colorear la barra de estado en el azul corporativo de la AppBar para reloj y batería
        window.statusBarColor = android.graphics.Color.parseColor("#1A4375")

        // Configurar Insets para soporte Edge-to-Edge (dejamos que el fondo azul fluya bajo la barra de estado)
        val vistaPrincipal = findViewById<View>(R.id.main)
        if (vistaPrincipal != null) {
            ViewCompat.setOnApplyWindowInsetsListener(vistaPrincipal) { v, insets ->
                val barrasSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                // Ajustamos el padding superior a 0 para que la AppBar ocupe el área de la barra de estado
                v.setPadding(barrasSistema.left, 0, barrasSistema.right, barrasSistema.bottom)
                insets
            }
        }

        // Inicializar vistas
        inicializarVistas()

        // Configurar iconos de registro desde drawables locales
        configurarIconosRegistro()

        // Configurar la fecha y hora dinámica del sistema
        configurarFechaYHora()

        // Configurar la sección colapsable de Notas Opcionales
        configurarSeccionNotas()

        // Configurar el evento del botón de Guardar
        configurarBotonGuardar()
    }

    private fun inicializarVistas() {
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro)
        etGlucosaValor = findViewById(R.id.etGlucosaValor)
        rgTipoRegistro = findViewById(R.id.rgTipoRegistro)
        layoutCabeceraNotas = findViewById(R.id.layoutCabeceraNotas)
        layoutCuerpoNotas = findViewById(R.id.layoutCuerpoNotas)
        tvFlechaNotas = findViewById(R.id.tvFlechaNotas)
        etNotas = findViewById(R.id.etNotas)
        btnGuardarRegistro = findViewById(R.id.btnGuardarRegistro)
    }

    private fun configurarFechaYHora() {
        try {
            val calendario = Calendar.getInstance()
            val configuracionRegional = Locale("es", "ES")

            // Nombre del día 
            val formateadorDia = SimpleDateFormat("EEEE", configuracionRegional)
            val diaSemana = formateadorDia.format(calendario.time).replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(configuracionRegional) else it.toString() 
            }

            // Fecha
            val formateadorFecha = SimpleDateFormat("d MMMM", configuracionRegional)
            val fecha = formateadorFecha.format(calendario.time).replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(configuracionRegional) else it.toString() 
            }

            // Hora
            val formateadorHora = SimpleDateFormat("h:mm a", configuracionRegional)
            val hora = formateadorHora.format(calendario.time).uppercase(configuracionRegional)

            // Armar el string en el formato exacto: REGISTRO: Hoy - [Día], [Fecha] | [Hora]
            val textoFechaCompleta = "REGISTRO: Hoy - $diaSemana, $fecha | $hora"
            tvFechaRegistro.text = textoFechaCompleta
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback estático en caso de cualquier error
            tvFechaRegistro.text = "REGISTRO: Hoy - Control de Glucosa"
        }
    }

    private fun configurarSeccionNotas() {
        layoutCabeceraNotas.setOnClickListener {
            notasExpandidas = !notasExpandidas
            if (notasExpandidas) {
                layoutCuerpoNotas.visibility = View.VISIBLE
                tvFlechaNotas.text = "▲"
            } else {
                layoutCuerpoNotas.visibility = View.GONE
                tvFlechaNotas.text = "▼"
            }
        }
    }

    private fun configurarBotonGuardar() {
        btnGuardarRegistro.setOnClickListener {
            val valorIngresado = etGlucosaValor.text.toString().trim()

            // 1. Validar que se haya ingresado el valor
            if (valorIngresado.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor, ingrese un valor de glucosa",
                    Toast.LENGTH_SHORT
                ).show()
                etGlucosaValor.requestFocus()
                return@setOnClickListener
            }

            // 2. Obtener el tipo de registro seleccionado
            val idRadioSeleccionado = rgTipoRegistro.checkedRadioButtonId
            val rbSeleccionado = findViewById<RadioButton>(idRadioSeleccionado)
            val tipoCompleto = rbSeleccionado?.text?.toString() ?: "Ayunas"
            
            // Extraer el texto limpio quitando el emoji si existiera
            val tipoSeleccionado = if (tipoCompleto.contains("  ")) {
                tipoCompleto.substringAfter("  ").trim()
            } else {
                tipoCompleto.trim()
            }

            // 3. Obtener notas opcionales
            val textoNotas = etNotas.text.toString().trim()
            val notasFinal = if (textoNotas.isEmpty()) "Sin notas" else textoNotas

            // 4. Construir el mensaje resumen con el formato requerido
            val mensajeResumen = """
                Registro guardado:
                $valorIngresado mg/dL
                Tipo: $tipoSeleccionado
                Notas: $notasFinal
            """.trimIndent()

            // 5. Mostrar retroalimentación inmediata mediante un Toast de Android
            Toast.makeText(this, mensajeResumen, Toast.LENGTH_LONG).show()
        }
    }

    private fun dpAPx(dp: Int): Int {
        val escala = resources.displayMetrics.density
        return (dp * escala + 0.5f).toInt()
    }

    private fun configurarIconosRegistro() {
        try {
            val tamanioIcono = dpAPx(28)

            // Ayunas
            val rbAyunas = findViewById<RadioButton>(R.id.rbAyunas)
            val imagenAyunas = resources.getDrawable(R.drawable.ayunas, theme)
            imagenAyunas.setBounds(0, 0, tamanioIcono, tamanioIcono)
            rbAyunas.setCompoundDrawablesRelative(imagenAyunas, null, null, null)

            // Antes de Almuerzo
            val rbAntesAlmuerzo = findViewById<RadioButton>(R.id.rbAntesAlmuerzo)
            val imagenAntes = resources.getDrawable(R.drawable.antes_del_almuerzo, theme)
            imagenAntes.setBounds(0, 0, tamanioIcono, tamanioIcono)
            rbAntesAlmuerzo.setCompoundDrawablesRelative(imagenAntes, null, null, null)

            // Después de Almuerzo
            val rbDespuesAlmuerzo = findViewById<RadioButton>(R.id.rbDespuesAlmuerzo)
            val imagenDespues = resources.getDrawable(R.drawable.despues_del_almuerzo, theme)
            imagenDespues.setBounds(0, 0, tamanioIcono, tamanioIcono)
            rbDespuesAlmuerzo.setCompoundDrawablesRelative(imagenDespues, null, null, null)

            // Cena
            val rbCena = findViewById<RadioButton>(R.id.rbCena)
            val imagenCena = resources.getDrawable(R.drawable.cena, theme)
            imagenCena.setBounds(0, 0, tamanioIcono, tamanioIcono)
            rbCena.setCompoundDrawablesRelative(imagenCena, null, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}