package pa.ac.utp.miprimeraapp

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecurityUtil {

    private const val KEY_ALIAS = "SaludAppDBKey"
    private const val PREFS_NAME = "SaludAppSecurity"
    private const val KEY_ENCRYPTED_PASS = "encrypted_db_pass"
    private const val KEY_IV = "db_pass_iv"

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun getOrCreateDBPassword(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encryptedPass = prefs.getString(KEY_ENCRYPTED_PASS, null)
        val ivString = prefs.getString(KEY_IV, null)

        if (encryptedPass != null && ivString != null) {
            try {
                // Decrypt and return
                return decryptString(encryptedPass, ivString)
            } catch (e: Exception) {
                // El Keystore fue borrado (ej: reinstalación) pero las SharedPreferences se restauraron.
                // Ignoramos el error y generamos una nueva contraseña.
            }
        }
        
        // Generate new random password
        val newPass = UUID.randomUUID().toString()
        val (encrypted, iv) = encryptString(newPass)
        
        prefs.edit()
            .putString(KEY_ENCRYPTED_PASS, encrypted)
            .putString(KEY_IV, iv)
            .apply()
            
        return newPass
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keySpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                // Not requiring user authentication here to avoid data loss if biometrics change
                .build()
            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
        }

        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    private fun encryptString(plainText: String): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Pair(Base64.encodeToString(encrypted, Base64.DEFAULT), Base64.encodeToString(iv, Base64.DEFAULT))
    }

    private fun decryptString(encryptedText: String, ivString: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = Base64.decode(ivString, Base64.DEFAULT)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decrypted = cipher.doFinal(encryptedBytes)
        return String(decrypted, Charsets.UTF_8)
    }
}
