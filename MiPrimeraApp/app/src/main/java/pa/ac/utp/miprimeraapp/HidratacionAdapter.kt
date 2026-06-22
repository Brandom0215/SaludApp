package pa.ac.utp.miprimeraapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class HidratacionAdapter(
    private val context: Context,
    private val dataSource: List<RegistroHidratacion>
) : BaseAdapter() {

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_historial_hidratacion, parent, false)

        val registro = getItem(position) as RegistroHidratacion

        val tvFecha = view.findViewById<TextView>(R.id.tvFecha)
        val tvVasos = view.findViewById<TextView>(R.id.tvVasos)
        val tvMeta = view.findViewById<TextView>(R.id.tvMeta)

        tvFecha.text = registro.fecha
        tvVasos.text = "${registro.vasos} Vasos (${registro.mililitros} ml)"

        if (registro.metaCumplida) {
            tvMeta.text = "¡Meta Cumplida!"
            tvMeta.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            val faltan = 8 - registro.vasos
            tvMeta.text = "- $faltan Vasos"
            tvMeta.setTextColor(Color.parseColor("#E74C3C"))
        }

        return view
    }
}
