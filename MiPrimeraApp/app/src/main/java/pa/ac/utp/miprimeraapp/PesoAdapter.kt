package pa.ac.utp.miprimeraapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.Locale

class PesoAdapter(private val context: Context, private val data: List<RegistroPeso>) :
    BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Any = data[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.item_historial_peso, parent, false)

        val item = data[position]

        val ivIcono = view.findViewById<ImageView>(R.id.ivIconoIMC)
        val tvFecha = view.findViewById<TextView>(R.id.tvFecha)
        val tvPesoIMC = view.findViewById<TextView>(R.id.tvPesoIMC)

        tvFecha.text = "Fecha: ${item.fecha}"
        tvPesoIMC.text = String.format(Locale.getDefault(), "Peso: %.1f kg | IMC: %.1f",
            item.peso, item.imc)

        // Lógica para determinar el nombre del icono basado en el IMC
        val nombreIcono = when {
            item.imc < 18.5 -> "bajopeso"
            item.imc < 25.0 -> "normal"
            item.imc < 30.0 -> "sobrepeso"
            item.imc < 35.0 -> "obesidad1"
            item.imc < 40.0 -> "obesidad2"
            else -> "obesidad3"
        }

        // Obtener el ID del recurso dinámicamente por nombre
        val resId = context.resources.getIdentifier(nombreIcono, "drawable",
            context.packageName)

        if (resId != 0) {
            ivIcono.setImageResource(resId)
        } else {
            // Fallback: icono por defecto si no se encuentran las imágenes
            ivIcono.setImageResource(android.R.drawable.ic_menu_info_details)
        }

        return view
    }
}
