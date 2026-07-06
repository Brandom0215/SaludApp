package pa.ac.utp.miprimeraapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val cardPeso = view.findViewById<MaterialCardView>(R.id.cardPeso)
        val cardPresion = view.findViewById<MaterialCardView>(R.id.cardPresion)
        val cardGlucosa = view.findViewById<MaterialCardView>(R.id.cardGlucosa)
        val cardActividad = view.findViewById<MaterialCardView>(R.id.cardActividad)
        val cardHidratacion = view.findViewById<MaterialCardView>(R.id.cardHidratacion)
        val cardMedicacion = view.findViewById<MaterialCardView>(R.id.cardMedicacion)


        cardPeso.setOnClickListener {
            Toast.makeText(requireContext(), "Accediendo a Peso IMC", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Peso::class.java)
            startActivity(intent)
        }

        cardPresion.setOnClickListener {
            Toast.makeText(requireContext(), "Accediendo a Presion Arterial", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Presion::class.java)
            startActivity(intent)
        }

        cardGlucosa.setOnClickListener {
            Toast.makeText(requireContext(), "Accediendo a Control de Glucosa", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Glucosa::class.java)
            startActivity(intent)
        }

        cardActividad.setOnClickListener {
            Toast.makeText(requireContext(), "Accediendo a Control de Actividad Fisica", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Actividad::class.java)
            startActivity(intent)
        }

        cardHidratacion.setOnClickListener {
            Toast.makeText(requireContext(), "Accediendo a Control de Hidratacion", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Hidratacion::class.java)
            startActivity(intent)
        }

        cardMedicacion.setOnClickListener {
            Toast.makeText(requireContext(), "Accediendo a Medicamentos", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Medicacion::class.java)
            startActivity(intent)
        }

        return view
    }
}
