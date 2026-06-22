package pa.ac.utp.miprimeraapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MedicamentoAdapter(
    private val context: Context,
    private val dataSource: MutableList<RegistroMedicamento>,
    private val onAction: (Int, String) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = dataSource.size
    override fun getItem(position: Int): Any = dataSource[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_medicamento, parent, false)

        val registro = getItem(position) as RegistroMedicamento

        val tvNombreMed = view.findViewById<TextView>(R.id.tvNombreMed)
        val tvDosisHora = view.findViewById<TextView>(R.id.tvDosisHora)
        val ivTomado = view.findViewById<ImageView>(R.id.ivTomado)
        val ivMenuOptions = view.findViewById<ImageView>(R.id.ivMenuOptions)

        tvNombreMed.text = registro.nombre
        tvDosisHora.text = "${registro.hora} - ${registro.dosis}"
        
        if (registro.tomado) {
            ivTomado.setImageResource(R.drawable.ic_check_circle)
            tvNombreMed.paintFlags = tvNombreMed.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            view.alpha = 0.5f
        } else {
            ivTomado.setImageResource(R.drawable.ic_circle_outline)
            tvNombreMed.paintFlags = tvNombreMed.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            view.alpha = 1.0f
        }

        ivTomado.setOnClickListener {
            registro.tomado = !registro.tomado
            if (registro.tomado) {
                ivTomado.setImageResource(R.drawable.ic_check_circle)
                tvNombreMed.paintFlags = tvNombreMed.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                view.alpha = 0.5f
            } else {
                ivTomado.setImageResource(R.drawable.ic_circle_outline)
                tvNombreMed.paintFlags = tvNombreMed.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                view.alpha = 1.0f
            }
            onAction(position, "TOGGLE")
        }

        ivMenuOptions.setOnClickListener { menuView ->
            val popup = android.widget.PopupMenu(context, menuView)
            popup.menu.add(0, 1, 0, "Editar")
            popup.menu.add(0, 2, 1, "Eliminar")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> onAction(position, "EDIT")
                    2 -> onAction(position, "DELETE")
                }
                true
            }
            popup.show()
        }

        return view
    }
}
