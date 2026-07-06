package pa.ac.utp.miprimeraapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Forzar modo claro desde el inicio
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        lifecycleScope.launch {
            delay(2000)
            val prefs = getSharedPreferences("SaludAppPrefs", Context.MODE_PRIVATE)
            val userId = prefs.getLong("user_id", -1L)
            
            if (userId != -1L) {
                val dbHelper = DatabaseHelper(this@Splash)
                val userExists = dbHelper.obtenerUsuario(userId) != null
                
                if (userExists) {
                    startActivity(Intent(this@Splash, MainActivity::class.java))
                } else {
                    // El usuario estaba en SharedPreferences pero no en la base de datos (Ej: Reinstalación con copia de seguridad)
                    prefs.edit().clear().apply()
                    startActivity(Intent(this@Splash, LoginActivity::class.java))
                }
            } else {
                startActivity(Intent(this@Splash, LoginActivity::class.java))
            }
            finish()
        }

    }
}