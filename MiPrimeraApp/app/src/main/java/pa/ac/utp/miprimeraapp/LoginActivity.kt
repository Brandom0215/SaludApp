package pa.ac.utp.miprimeraapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val etUsername = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val user = etUsername.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashPass = SecurityUtil.hashPassword(pass)
            val userId = dbHelper.validarUsuario(user, hashPass)

            if (userId != -1L) {
                showBiometricPrompt(userId, user)
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showBiometricPrompt(userId: Long, user: String) {
        val biometricManager = androidx.biometric.BiometricManager.from(this)
        when (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = androidx.core.content.ContextCompat.getMainExecutor(this)
                val biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
                    object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            proceedToMain(userId, user)
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(applicationContext, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                        }
                    })

                val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Autenticación Requerida")
                    .setSubtitle("Verifica tu identidad para acceder a tus datos de salud")
                    .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }
            else -> {
                // Si no hay biometría configurada, continuar
                proceedToMain(userId, user)
            }
        }
    }

    private fun proceedToMain(userId: Long, user: String) {
        val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("user_id", userId).apply()
        prefs.edit().putString("username", user).apply()

        dbHelper.registrarBitacora(userId, "Inicio de sesión")

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
