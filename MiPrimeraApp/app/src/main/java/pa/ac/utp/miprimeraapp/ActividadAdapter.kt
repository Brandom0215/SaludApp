package pa.ac.utp.miprimeraapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

data class RegistroActividadList(val fecha: String, val pasos: Int, val calorias: Int, val minutos: Int)

class ActividadAdapter(
    private val context: Context, 
    private val data: List<RegistroActividadList>,
    private val onDeleteClick: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Any = data[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_historial_actividad, parent, false)

        val item = data[position]

        val ivIcono = view.findViewById<ImageView>(R.id.ivIconoActividad)
        val tvFecha = view.findViewById<TextView>(R.id.tvFechaActividad)
        val tvDetalle = view.findViewById<TextView>(R.id.tvDetalleActividad)

        tvFecha.text = item.fecha
        tvDetalle.text = "${item.pasos} Pasos | ${item.calorias} Kcal | ${item.minutos} min"

        // Lógica de imágenes basada en el tiempo de actividad (minutos)
        val nombreIcono = when {
            item.minutos < 30 -> "act_baja"
            item.minutos < 60 -> "act_moderada"
            else -> "act_alta"
        }

        val resId = context.resources.getIdentifier(nombreIcono, "drawable", context.packageName)

        if (resId != 0) {
            ivIcono.setImageResource(resId)
        } else {
            // Fallback si la imagen no existe
            ivIcono.setImageResource(android.R.drawable.ic_menu_info_details)
        }

        val btnEliminar = view.findViewById<ImageView>(R.id.btnEliminar)
        btnEliminar.setOnClickListener {
            onDeleteClick(position)
        }

        return view
    }
}
