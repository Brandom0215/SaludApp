package pa.ac.utp.miprimeraapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ImageView

class GlucosaAdapter(
    private val context: Context,
    private val listaGlucosa: List<RegistroGlucosaDB>,
    private val onDeleteClick: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return listaGlucosa.size
    }

    override fun getItem(position: Int): Any {
        return listaGlucosa[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_historial_glucosa, parent, false)

        val tvFecha = view.findViewById<TextView>(R.id.tvFecha)
        val tvGlucosa = view.findViewById<TextView>(R.id.tvGlucosa)
        val tvTipo = view.findViewById<TextView>(R.id.tvTipo)
        val tvNotas = view.findViewById<TextView>(R.id.tvNotas)

        val registro = listaGlucosa[position]

        tvFecha.text = registro.fecha
        tvGlucosa.text = "${registro.valor} mg/dL"
        tvTipo.text = registro.tipo
        tvNotas.text = registro.notas

        val btnEliminar = view.findViewById<ImageView>(R.id.btnEliminar)
        btnEliminar.setOnClickListener {
            onDeleteClick(position)
        }

        return view
    }
}
