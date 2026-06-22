package pa.ac.utp.miprimeraapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class RegistroMedicamento(val nombre: String, val hora: String, val dosis: String, var tomado: Boolean)

class Medicacion : AppCompatActivity() {

    private val listaMedicamentos = mutableListOf<RegistroMedicamento>()
    private lateinit var adapter: MedicamentoAdapter

    private fun ordenarLista() {
        listaMedicamentos.sortBy { it.tomado }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val isEdit = data.getBooleanExtra("isEdit", false)
                val nombre = data.getStringExtra("nombre") ?: ""
                val hora = data.getStringExtra("hora") ?: ""
                val dosis = data.getStringExtra("dosis") ?: ""
                
                if (isEdit) {
                    val pos = data.getIntExtra("position", -1)
                    if (pos != -1) {
                        listaMedicamentos[pos] = RegistroMedicamento(nombre, hora, dosis, listaMedicamentos[pos].tomado)
                    }
                } else {
                    listaMedicamentos.add(RegistroMedicamento(nombre, hora, dosis, false))
                }
                ordenarLista()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            finish()
        }

        // Datos iniciales de prueba
        listaMedicamentos.add(RegistroMedicamento("Paracetamol 500mg", "08:00 AM", "1 Pastilla", true))
        listaMedicamentos.add(RegistroMedicamento("Losartán 50mg", "08:00 PM", "1 Pastilla", false))
        ordenarLista()

        val lvMedicamentos = findViewById<ListView>(R.id.lvMedicamentos)
        adapter = MedicamentoAdapter(this, listaMedicamentos) { position, action ->
            if (action == "DELETE") {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar medicamento")
                    .setMessage("¿Estás seguro de que deseas eliminar este medicamento de tu rutina?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        listaMedicamentos.removeAt(position)
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else if (action == "EDIT") {
                val med = listaMedicamentos[position]
                val intent = Intent(this, AgregarMedicamento::class.java)
                intent.putExtra("isEdit", true)
                intent.putExtra("position", position)
                intent.putExtra("nombre", med.nombre)
                intent.putExtra("horaStr", med.hora)
                intent.putExtra("dosisStr", med.dosis)
                resultLauncher.launch(intent)
            } else if (action == "TOGGLE") {
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    ordenarLista()
                    adapter.notifyDataSetChanged()
                }, 400)
            }
        }
        lvMedicamentos.adapter = adapter

        val btnAgregarMedicamento = findViewById<Button>(R.id.btnAgregarMedicamento)
        btnAgregarMedicamento.setOnClickListener {
            val intent = Intent(this, AgregarMedicamento::class.java)
            resultLauncher.launch(intent)
        }
    }
}