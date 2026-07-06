package pa.ac.utp.miprimeraapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ImageView

class PresionAdapter(
    private val context: Context,
    private val listaPresiones: List<RegistroPresionDB>,
    private val onDeleteClick: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return listaPresiones.size
    }

    override fun getItem(position: Int): Any {
        return listaPresiones[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_historial_presion, parent, false)

        val tvFecha = view.findViewById<TextView>(R.id.tvFecha)
        val tvPresion = view.findViewById<TextView>(R.id.tvPresion)
        val tvPulso = view.findViewById<TextView>(R.id.tvPulso)

        val registro = listaPresiones[position]

        tvFecha.text = registro.fecha
        tvPresion.text = "${registro.sistolica}/${registro.diastolica} mmHg"
        tvPulso.text = "${registro.pulso} BPM"

        val btnEliminar = view.findViewById<ImageView>(R.id.btnEliminar)
        btnEliminar.setOnClickListener {
            onDeleteClick(position)
        }

        return view
    }
}
