package pa.ac.utp.miprimeraapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class PerfilFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private var currentUserId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        dbHelper = DatabaseHelper(requireContext())
        val prefs = requireActivity().getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("user_id", -1L)

        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnDeleteAccount = view.findViewById<Button>(R.id.btnDeleteAccount)
        val btnTerminos = view.findViewById<Button>(R.id.btnTerminos)
        val tvPerfilIMC = view.findViewById<TextView>(R.id.tvPerfilIMC)
        val tvPerfilIMCCategoria = view.findViewById<TextView>(R.id.tvPerfilIMCCategoria)
        val tvPerfilPasos = view.findViewById<TextView>(R.id.tvPerfilPasos)
        val tvPerfilAgua = view.findViewById<TextView>(R.id.tvPerfilAgua)

        if (currentUserId != -1L) {
            val username = dbHelper.obtenerUsuario(currentUserId)
            val correo = dbHelper.obtenerCorreo(currentUserId)
            tvUsername.text = username ?: "Usuario"
            tvUserEmail.text = "Correo: ${correo ?: "No registrado"}"
            
            val ultimoIMC = dbHelper.obtenerUltimoIMC(currentUserId)
            if (ultimoIMC != null) {
                tvPerfilIMC.text = String.format("%.1f", ultimoIMC)
                val categoria = when {
                    ultimoIMC < 18.5 -> "Bajo peso"
                    ultimoIMC < 25.0 -> "Normal"
                    ultimoIMC < 30.0 -> "Sobrepeso"
                    else -> "Obesidad"
                }
                tvPerfilIMCCategoria.text = categoria
            } else {
                tvPerfilIMC.text = "--"
                tvPerfilIMCCategoria.text = "Sin datos"
            }
            
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val fechaHoy = sdf.format(java.util.Date())
            
            val pasosHoy = dbHelper.obtenerPasos(currentUserId, fechaHoy)
            tvPerfilPasos.text = pasosHoy.toString()
            
            val aguaHoy = dbHelper.obtenerMlHoy(currentUserId)
            tvPerfilAgua.text = "${aguaHoy} ml"
        }

        btnChangePassword.setOnClickListener {
            mostrarDialogoCambiarPassword()
        }

        btnTerminos.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_terminos, null)
            val dialog = android.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()
                
            dialogView.findViewById<Button>(R.id.btnCerrarTerminos).setOnClickListener {
                dialog.dismiss()
            }
            
            // Hacer el fondo transparente para que se vea el bg_glass_card
            dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.show()
        }

        btnLogout.setOnClickListener {
            // Cerrar sesión
            prefs.edit().clear().apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        btnDeleteAccount.setOnClickListener {
            // Confirmación de Derechos ARCO (Cancelación)
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Cuenta")
                .setMessage("¿Estás seguro de que deseas eliminar tu cuenta y todos los datos asociados de forma permanente? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar") { _, _ ->
                    dbHelper.eliminarCuenta(currentUserId)
                    prefs.edit().clear().apply()
                    Toast.makeText(requireContext(), "Cuenta eliminada permanentemente", Toast.LENGTH_LONG).show()
                    
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        return view
    }

    private fun mostrarDialogoCambiarPassword() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cambiar_password, null)
        val etOldPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etOldPassword)
        val etNewPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNewPassword)
        val etConfirmNewPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etConfirmNewPassword)
        val btnCancelDialog = dialogView.findViewById<Button>(R.id.btnCancelDialog)
        val btnConfirmDialog = dialogView.findViewById<Button>(R.id.btnConfirmDialog)

        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancelDialog.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmDialog.setOnClickListener {
            val oldPass = etOldPassword.text.toString().trim()
            val newPass = etNewPassword.text.toString().trim()
            val confirmPass = etConfirmNewPassword.text.toString().trim()

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(requireContext(), "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(requireContext(), "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val username = dbHelper.obtenerUsuario(currentUserId) ?: ""
            val oldHash = SecurityUtil.hashPassword(oldPass)

            val checkId = dbHelper.validarUsuario(username, oldHash)
            if (checkId == currentUserId) {
                val newHash = SecurityUtil.hashPassword(newPass)
                val actualizado = dbHelper.actualizarPassword(currentUserId, newHash)
                if (actualizado) {
                    Toast.makeText(requireContext(), "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                    dbHelper.registrarBitacora(currentUserId, "Cambió su contraseña")
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
