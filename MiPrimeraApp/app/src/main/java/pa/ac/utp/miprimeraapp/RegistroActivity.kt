package pa.ac.utp.miprimeraapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        dbHelper = DatabaseHelper(this)

        val etUsername = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNewUsername)
        val etNombre = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombre)
        val etApellido = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etApellido)
        val etEdad = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEdad)
        val etEmail = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etConfirmPassword)
        val cbConsentimiento = findViewById<CheckBox>(R.id.cbConsentimiento)
        val tvLeerTerminos = findViewById<TextView>(R.id.tvLeerTerminos)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        tvLeerTerminos.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_terminos, null)
            val dialog = android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create()
                
            dialogView.findViewById<Button>(R.id.btnCerrarTerminos).setOnClickListener {
                dialog.dismiss()
            }
            
            // Hacer el fondo transparente para que se vea el bg_glass_card
            dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.show()
        }

        btnRegister.setOnClickListener {
            val user = etUsername.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val edadStr = etEdad.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()
            val passConf = etConfirmPassword.text.toString().trim()
            val consentimiento = cbConsentimiento.isChecked

            if (user.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || edadStr.isEmpty() || email.isEmpty() || pass.isEmpty() || passConf.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val edad = edadStr.toIntOrNull()
            if (edad == null || edad <= 0) {
                Toast.makeText(this, "Por favor ingresa una edad válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Por favor ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != passConf) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!consentimiento) {
                Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.verificarSiExiste(user)) {
                Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashPass = SecurityUtil.hashPassword(pass)
            val userId = dbHelper.registrarUsuario(user, hashPass, email, nombre, apellido, edad, consentimiento)

            if (userId != -1L) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                dbHelper.registrarBitacora(userId, "Creación de cuenta y aceptación de términos")
                finish() // Volver al login
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }
}
