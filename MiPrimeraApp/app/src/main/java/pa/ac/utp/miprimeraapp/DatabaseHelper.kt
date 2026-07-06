package pa.ac.utp.miprimeraapp

import android.content.ContentValues
import android.content.Context
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val dbPassphrase by lazy { SecurityUtil.getOrCreateDBPassword(context) }

    init {
        SQLiteDatabase.loadLibs(context)
    }

    val safeWritableDatabase: SQLiteDatabase
        get() = try { getWritableDatabase(dbPassphrase) } catch (e: Exception) { context.deleteDatabase(DATABASE_NAME); getWritableDatabase(dbPassphrase) }

    val safeReadableDatabase: SQLiteDatabase
        get() = try { getReadableDatabase(dbPassphrase) } catch (e: Exception) { context.deleteDatabase(DATABASE_NAME); getReadableDatabase(dbPassphrase) }

    companion object {
        private const val DATABASE_NAME = "MiPrimeraApp.db"
        private const val DATABASE_VERSION = 13

        // Tabla Usuarios
        const val TABLE_USUARIOS = "Usuarios"
        const val COL_USER_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password_hash"
        const val COL_EMAIL = "correo"
        const val COL_NOMBRE = "nombre"
        const val COL_APELLIDO = "apellido"
        const val COL_EDAD = "edad"
        const val COL_CONSENTIMIENTO = "consentimiento"

        // Tabla Bitacora
        const val TABLE_BITACORA = "Bitacora"
        const val COL_BIT_ID = "id"
        const val COL_BIT_USER_ID = "user_id"
        const val COL_BIT_ACCION = "accion"
        const val COL_BIT_FECHA = "fecha"

        // Tabla Actividades
        const val TABLE_ACTIVIDADES = "Actividades"
        const val COL_ACT_ID = "id"
        const val COL_ACT_USER_ID = "user_id"
        const val COL_ACT_NOMBRE = "nombre_act"
        const val COL_ACT_MINUTOS = "minutos"
        const val COL_ACT_INTENSIDAD = "intensidad"
        const val COL_ACT_FECHA = "fecha"

        // Tabla Pesos
        const val TABLE_PESOS = "Pesos"
        const val COL_PESO_ID = "id"
        const val COL_PESO_USER_ID = "user_id"
        const val COL_PESO_VALOR = "peso"
        const val COL_PESO_IMC = "imc"
        const val COL_PESO_FECHA = "fecha"

        // Tabla Presiones
        const val TABLE_PRESIONES = "Presiones"
        const val COL_PRES_ID = "id"
        const val COL_PRES_USER_ID = "user_id"
        const val COL_PRES_SISTOLICA = "sistolica"
        const val COL_PRES_DIASTOLICA = "diastolica"
        const val COL_PRES_PULSO = "pulso"
        const val COL_PRES_FECHA = "fecha"

        // Tabla Hidratacion
        const val TABLE_HIDRATACION = "Hidratacion"
        const val COL_HIDR_ID = "id"
        const val COL_HIDR_USER_ID = "user_id"
        const val COL_HIDR_VASOS = "vasos"
        const val COL_HIDR_FECHA = "fecha"

        // Tabla Glucosa
        const val TABLE_GLUCOSA = "Glucosa"
        const val COL_GLUC_ID = "id"
        const val COL_GLUC_USER_ID = "user_id"
        const val COL_GLUC_VALOR = "valor"
        const val COL_GLUC_TIPO = "tipo"
        const val COL_GLUC_NOTAS = "notas"
        const val COL_GLUC_FECHA = "fecha"

        // Tabla Medicacion
        const val TABLE_MEDICAMENTOS = "Medicamentos"
        const val COL_MED_ID = "id"
        const val COL_MED_USER_ID = "user_id"
        const val COL_MED_NOMBRE = "nombre_med"
        const val COL_MED_DOSIS = "dosis"
        const val COL_MED_FRECUENCIA = "frecuencia"
        const val COL_MED_HORA = "hora"
        const val COL_MED_TOMADO = "tomado"
        const val COL_MED_FECHA_TOMADO = "fecha_tomado"

        // Tabla Pasos Diarios
        const val TABLE_PASOS = "PasosDiarios"
        const val COL_PASOS_ID = "id"
        const val COL_PASOS_USER_ID = "user_id"
        const val COL_PASOS_FECHA = "fecha"
        const val COL_PASOS_CANTIDAD = "cantidad"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsuarios = ("CREATE TABLE " + TABLE_USUARIOS + "("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USERNAME + " TEXT UNIQUE,"
                + COL_PASSWORD + " TEXT,"
                + COL_EMAIL + " TEXT,"
                + COL_NOMBRE + " TEXT,"
                + COL_APELLIDO + " TEXT,"
                + COL_EDAD + " INTEGER,"
                + COL_CONSENTIMIENTO + " INTEGER" + ")")
        db.execSQL(createUsuarios)

        val createBitacora = ("CREATE TABLE " + TABLE_BITACORA + "("
                + COL_BIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_BIT_USER_ID + " INTEGER,"
                + COL_BIT_ACCION + " TEXT,"
                + COL_BIT_FECHA + " TEXT" + ")")
        db.execSQL(createBitacora)

        val createActividades = ("CREATE TABLE " + TABLE_ACTIVIDADES + "("
                + COL_ACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_ACT_USER_ID + " INTEGER,"
                + COL_ACT_NOMBRE + " TEXT,"
                + COL_ACT_MINUTOS + " INTEGER,"
                + COL_ACT_INTENSIDAD + " TEXT,"
                + COL_ACT_FECHA + " TEXT" + ")")
        db.execSQL(createActividades)

        val createPesos = ("CREATE TABLE " + TABLE_PESOS + "("
                + COL_PESO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_PESO_USER_ID + " INTEGER,"
                + COL_PESO_VALOR + " REAL,"
                + COL_PESO_IMC + " REAL,"
                + COL_PESO_FECHA + " TEXT" + ")")
        db.execSQL(createPesos)

        val createPresiones = ("CREATE TABLE " + TABLE_PRESIONES + "("
                + COL_PRES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_PRES_USER_ID + " INTEGER,"
                + COL_PRES_SISTOLICA + " INTEGER,"
                + COL_PRES_DIASTOLICA + " INTEGER,"
                + COL_PRES_PULSO + " INTEGER,"
                + COL_PRES_FECHA + " TEXT" + ")")
        db.execSQL(createPresiones)

        val createHidratacion = ("CREATE TABLE " + TABLE_HIDRATACION + "("
                + COL_HIDR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_HIDR_USER_ID + " INTEGER,"
                + COL_HIDR_VASOS + " INTEGER,"
                + COL_HIDR_FECHA + " TEXT" + ")")
        db.execSQL(createHidratacion)

        val createGlucosa = ("CREATE TABLE " + TABLE_GLUCOSA + "("
                + COL_GLUC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_GLUC_USER_ID + " INTEGER,"
                + COL_GLUC_VALOR + " INTEGER,"
                + COL_GLUC_TIPO + " TEXT,"
                + COL_GLUC_NOTAS + " TEXT,"
                + COL_GLUC_FECHA + " TEXT" + ")")
        db.execSQL(createGlucosa)

        val createMedicamentos = ("CREATE TABLE " + TABLE_MEDICAMENTOS + "("
                + COL_MED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MED_USER_ID + " INTEGER,"
                + COL_MED_NOMBRE + " TEXT,"
                + COL_MED_HORA + " TEXT,"
                + COL_MED_DOSIS + " TEXT,"
                + COL_MED_TOMADO + " INTEGER,"
                + COL_MED_FECHA_TOMADO + " TEXT" + ")")
        db.execSQL(createMedicamentos)
        
        val createPasos = ("CREATE TABLE " + TABLE_PASOS + "("
                + COL_PASOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_PASOS_USER_ID + " INTEGER,"
                + COL_PASOS_FECHA + " TEXT,"
                + COL_PASOS_CANTIDAD + " INTEGER" + ")")
        db.execSQL(createPasos)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 10) {
            val createPasos = ("CREATE TABLE IF NOT EXISTS $TABLE_PASOS ("
                    + "$COL_PASOS_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$COL_PASOS_USER_ID INTEGER,"
                    + "$COL_PASOS_FECHA TEXT,"
                    + "$COL_PASOS_CANTIDAD INTEGER,"
                    + "FOREIGN KEY($COL_PASOS_USER_ID) REFERENCES $TABLE_USUARIOS($COL_USER_ID))")
            db.execSQL(createPasos)
        }
        if (oldVersion < 11) {
            // Convertir vasos a mililitros (1 vaso = 250ml)
            db.execSQL("UPDATE $TABLE_HIDRATACION SET $COL_HIDR_VASOS = $COL_HIDR_VASOS * 250")
        }
        if (oldVersion < 12) {
            db.execSQL("ALTER TABLE $TABLE_MEDICAMENTOS ADD COLUMN $COL_MED_FECHA_TOMADO TEXT")
        }
        if (oldVersion < 13) {
            val createPasos = ("CREATE TABLE IF NOT EXISTS $TABLE_PASOS ("
                    + "$COL_PASOS_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$COL_PASOS_USER_ID INTEGER,"
                    + "$COL_PASOS_FECHA TEXT,"
                    + "$COL_PASOS_CANTIDAD INTEGER,"
                    + "FOREIGN KEY($COL_PASOS_USER_ID) REFERENCES $TABLE_USUARIOS($COL_USER_ID))")
            db.execSQL(createPasos)
        }
    }

    // --- Métodos de Usuario ---
    fun registrarUsuario(username: String, passwordHash: String, email: String, nombre: String, apellido: String, edad: Int, consentimiento: Boolean): Long {
        val db = this.safeWritableDatabase
        val values = ContentValues()
        values.put(COL_USERNAME, username)
        values.put(COL_PASSWORD, passwordHash)
        values.put(COL_EMAIL, email)
        values.put(COL_NOMBRE, nombre)
        values.put(COL_APELLIDO, apellido)
        values.put(COL_EDAD, edad)
        values.put(COL_CONSENTIMIENTO, if (consentimiento) 1 else 0)

        val id = db.insert(TABLE_USUARIOS, null, values)
        db.close()
        return id
    }

    fun obtenerCorreo(userId: Long): String? {
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_EMAIL FROM $TABLE_USUARIOS WHERE $COL_USER_ID = ?", arrayOf(userId.toString()))
        var correo: String? = null
        if (cursor.moveToFirst()) {
            correo = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return correo
    }

    // Método para cambiar contraseña
    fun actualizarPassword(userId: Long, newPasswordHash: String): Boolean {
        val db = this.safeWritableDatabase
        val values = ContentValues().apply {
            put(COL_PASSWORD, newPasswordHash)
        }
        val result = db.update(TABLE_USUARIOS, values, "$COL_USER_ID=?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }

    fun validarUsuario(username: String, passwordHash: String): Long {
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_USER_ID FROM $TABLE_USUARIOS WHERE $COL_USERNAME = ? AND $COL_PASSWORD = ?", arrayOf(username, passwordHash))
        var userId: Long = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(0)
        }
        cursor.close()
        db.close()
        return userId
    }

    fun verificarSiExiste(username: String): Boolean {
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_USER_ID FROM $TABLE_USUARIOS WHERE $COL_USERNAME = ?", arrayOf(username))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun obtenerUsuario(userId: Long): String? {
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_USERNAME FROM $TABLE_USUARIOS WHERE $COL_USER_ID = ?", arrayOf(userId.toString()))
        var username: String? = null
        if (cursor.moveToFirst()) {
            username = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return username
    }

    fun eliminarCuenta(userId: Long) {
        val db = this.safeWritableDatabase
        // Eliminar actividades
        db.delete(TABLE_ACTIVIDADES, "$COL_ACT_USER_ID = ?", arrayOf(userId.toString()))
        // Eliminar pesos
        db.delete(TABLE_PESOS, "$COL_PESO_USER_ID = ?", arrayOf(userId.toString()))
        // Eliminar presiones
        db.delete(TABLE_PRESIONES, "$COL_PRES_USER_ID = ?", arrayOf(userId.toString()))
        // Eliminar hidratacion
        db.delete(TABLE_HIDRATACION, "$COL_HIDR_USER_ID = ?", arrayOf(userId.toString()))
        // Eliminar glucosa
        db.delete(TABLE_GLUCOSA, "$COL_GLUC_USER_ID = ?", arrayOf(userId.toString()))
        // Eliminar medicamentos
        db.delete(TABLE_MEDICAMENTOS, "$COL_MED_USER_ID = ?", arrayOf(userId.toString()))
        // Eliminar pasos
        db.delete(TABLE_PASOS, "$COL_PASOS_USER_ID = ?", arrayOf(userId.toString()))
        // Eliminar bitácora
        db.delete(TABLE_BITACORA, "$COL_BIT_USER_ID = ?", arrayOf(userId.toString()))
        // Finalmente, eliminar usuario
        db.delete(TABLE_USUARIOS, "$COL_USER_ID = ?", arrayOf(userId.toString()))
        db.close()
    }

    // --- Métodos de Bitácora ---
    fun registrarBitacora(userId: Long, accion: String) {
        val db = this.safeWritableDatabase
        val values = ContentValues()
        values.put(COL_BIT_USER_ID, userId)
        values.put(COL_BIT_ACCION, accion)
        
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        values.put(COL_BIT_FECHA, sdf.format(Date()))

        db.insert(TABLE_BITACORA, null, values)
        db.close()
    }

    // --- Métodos de Actividad ---
    fun registrarActividad(userId: Long, nombre: String, minutos: Int, intensidad: String): Long {
        val db = this.safeWritableDatabase
        val values = ContentValues()
        values.put(COL_ACT_USER_ID, userId)
        values.put(COL_ACT_NOMBRE, nombre)
        values.put(COL_ACT_MINUTOS, minutos)
        values.put(COL_ACT_INTENSIDAD, intensidad)
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        values.put(COL_ACT_FECHA, sdf.format(Date()))

        val id = db.insert(TABLE_ACTIVIDADES, null, values)
        db.close()
        
        // Registrar bitácora automáticamente
        registrarBitacora(userId, "Registró actividad: $nombre ($minutos min)")
        return id
    }

    fun obtenerActividadesHoy(userId: Long): List<RegistroActividadDB> {
        val lista = mutableListOf<RegistroActividadDB>()
        val db = this.safeReadableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())

        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACTIVIDADES WHERE $COL_ACT_USER_ID = ? AND $COL_ACT_FECHA = ?", arrayOf(userId.toString(), fechaHoy))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ACT_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACT_NOMBRE))
                val minutos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACT_MINUTOS))
                val intensidad = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACT_INTENSIDAD))
                lista.add(RegistroActividadDB(id, nombre, minutos, intensidad))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun eliminarActividad(actividadId: Long, userId: Long) {
        val db = this.safeWritableDatabase
        db.delete(TABLE_ACTIVIDADES, "$COL_ACT_ID = ?", arrayOf(actividadId.toString()))
        db.close()
        
        registrarBitacora(userId, "Eliminó una actividad")
    }

    fun eliminarPeso(pesoId: Long) {
        val db = this.safeWritableDatabase
        db.delete(TABLE_PESOS, "$COL_PESO_ID = ?", arrayOf(pesoId.toString()))
        db.close()
    }

    fun eliminarPresion(presionId: Long) {
        val db = this.safeWritableDatabase
        db.delete(TABLE_PRESIONES, "$COL_PRES_ID = ?", arrayOf(presionId.toString()))
        db.close()
    }

    fun eliminarHidratacion(hidratacionId: Long) {
        val db = this.safeWritableDatabase
        db.delete(TABLE_HIDRATACION, "$COL_HIDR_ID = ?", arrayOf(hidratacionId.toString()))
        db.close()
    }

    fun eliminarGlucosa(glucosaId: Long) {
        val db = this.safeWritableDatabase
        db.delete(TABLE_GLUCOSA, "$COL_GLUC_ID = ?", arrayOf(glucosaId.toString()))
        db.close()
    }


    // --- Métodos de Peso ---
    fun registrarPeso(userId: Long, peso: Double, imc: Double): Long {
        val db = this.safeWritableDatabase
        val values = ContentValues()
        values.put(COL_PESO_USER_ID, userId)
        values.put(COL_PESO_VALOR, peso)
        values.put(COL_PESO_IMC, imc)
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        values.put(COL_PESO_FECHA, sdf.format(Date()))

        val id = db.insert(TABLE_PESOS, null, values)
        db.close()
        
        registrarBitacora(userId, "Registró nuevo peso: $peso kg")
        return id
    }

    fun obtenerHistorialPesos(userId: Long): List<RegistroPesoDB> {
        val lista = mutableListOf<RegistroPesoDB>()
        val db = this.safeReadableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_PESOS WHERE $COL_PESO_USER_ID = ? ORDER BY $COL_PESO_ID DESC", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PESO_ID))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_PESO_FECHA))
                val peso = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PESO_VALOR))
                val imc = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PESO_IMC))
                lista.add(RegistroPesoDB(id, fecha, peso, imc))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    // --- Métodos de Presión ---
    fun registrarPresion(userId: Long, sistolica: Int, diastolica: Int, pulso: Int): Long {
        val db = this.safeWritableDatabase
        val values = ContentValues()
        values.put(COL_PRES_USER_ID, userId)
        values.put(COL_PRES_SISTOLICA, sistolica)
        values.put(COL_PRES_DIASTOLICA, diastolica)
        values.put(COL_PRES_PULSO, pulso)
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        values.put(COL_PRES_FECHA, sdf.format(Date()))

        val id = db.insert(TABLE_PRESIONES, null, values)
        db.close()
        
        registrarBitacora(userId, "Registró presión arterial: $sistolica/$diastolica")
        return id
    }

    fun obtenerHistorialPresion(userId: Long): List<RegistroPresionDB> {
        val lista = mutableListOf<RegistroPresionDB>()
        val db = this.safeReadableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRESIONES WHERE $COL_PRES_USER_ID = ? ORDER BY $COL_PRES_ID DESC", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PRES_ID))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRES_FECHA))
                val sistolica = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRES_SISTOLICA))
                val diastolica = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRES_DIASTOLICA))
                val pulso = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRES_PULSO))
                lista.add(RegistroPresionDB(id, fecha, sistolica, diastolica, pulso))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    // --- Métodos de Hidratación ---
    fun registrarHidratacion(userId: Long, ml: Int) {
        val db = this.safeWritableDatabase
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())

        // Verificar si ya existe registro hoy
        val cursor = db.rawQuery("SELECT $COL_HIDR_VASOS FROM $TABLE_HIDRATACION WHERE $COL_HIDR_USER_ID = ? AND $COL_HIDR_FECHA = ?", arrayOf(userId.toString(), fechaHoy))
        
        if (cursor.moveToFirst()) {
            val mlActuales = cursor.getInt(0)
            var nuevosMl = mlActuales + ml
            if (nuevosMl < 0) nuevosMl = 0 // Evitar valores negativos
            
            val values = ContentValues()
            values.put(COL_HIDR_VASOS, nuevosMl)
            db.update(TABLE_HIDRATACION, values, "$COL_HIDR_USER_ID = ? AND $COL_HIDR_FECHA = ?", arrayOf(userId.toString(), fechaHoy))
        } else {
            val values = ContentValues()
            var inicial = ml
            if (inicial < 0) inicial = 0
            
            values.put(COL_HIDR_USER_ID, userId)
            values.put(COL_HIDR_VASOS, inicial) // Ahora representa ML
            values.put(COL_HIDR_FECHA, fechaHoy)
            db.insert(TABLE_HIDRATACION, null, values)
        }
        cursor.close()
        db.close()
        
        registrarBitacora(userId, "Registró consumo de agua: $ml ml")
    }

    fun obtenerMlHoy(userId: Long): Int {
        val db = this.safeReadableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())

        val cursor = db.rawQuery("SELECT $COL_HIDR_VASOS FROM $TABLE_HIDRATACION WHERE $COL_HIDR_USER_ID = ? AND $COL_HIDR_FECHA = ?", arrayOf(userId.toString(), fechaHoy))
        
        var ml = 0
        if (cursor.moveToFirst()) {
            ml = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return ml
    }

    fun obtenerHistorialHidratacion(userId: Long): List<RegistroHidratacionDB> {
        val lista = mutableListOf<RegistroHidratacionDB>()
        val db = this.safeReadableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_HIDRATACION WHERE $COL_HIDR_USER_ID = ? ORDER BY $COL_HIDR_ID DESC", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_HIDR_ID))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_HIDR_FECHA))
                val vasos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HIDR_VASOS))
                lista.add(RegistroHidratacionDB(id, fecha, vasos))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    // --- Métodos de Glucosa ---
    fun registrarGlucosa(userId: Long, valor: Int, tipo: String, notas: String): Long {
        val db = this.safeWritableDatabase
        val values = ContentValues()
        values.put(COL_GLUC_USER_ID, userId)
        values.put(COL_GLUC_VALOR, valor)
        values.put(COL_GLUC_TIPO, tipo)
        values.put(COL_GLUC_NOTAS, notas)
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        values.put(COL_GLUC_FECHA, sdf.format(Date()))

        val id = db.insert(TABLE_GLUCOSA, null, values)
        db.close()
        
        registrarBitacora(userId, "Registró glucosa: $valor mg/dL ($tipo)")
        return id
    }

    fun obtenerHistorialGlucosa(userId: Long): List<RegistroGlucosaDB> {
        val lista = mutableListOf<RegistroGlucosaDB>()
        val db = this.safeReadableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_GLUCOSA WHERE $COL_GLUC_USER_ID = ? ORDER BY $COL_GLUC_ID DESC", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_GLUC_ID))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_GLUC_FECHA))
                val valor = cursor.getInt(cursor.getColumnIndexOrThrow(COL_GLUC_VALOR))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow(COL_GLUC_TIPO))
                val notas = cursor.getString(cursor.getColumnIndexOrThrow(COL_GLUC_NOTAS))
                lista.add(RegistroGlucosaDB(id, fecha, valor, tipo, notas))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    // --- Métodos de Medicamentos ---
    fun registrarMedicamento(userId: Long, nombre: String, hora: String, dosis: String): Long {
        val db = this.safeWritableDatabase
        val values = ContentValues()
        values.put(COL_MED_USER_ID, userId)
        values.put(COL_MED_NOMBRE, nombre)
        values.put(COL_MED_HORA, hora)
        values.put(COL_MED_DOSIS, dosis)
        values.put(COL_MED_TOMADO, 0)
        values.put(COL_MED_FECHA_TOMADO, "")

        val id = db.insert(TABLE_MEDICAMENTOS, null, values)
        db.close()
        
        registrarBitacora(userId, "Añadió medicamento: $nombre")
        return id
    }

    fun obtenerMedicamentos(userId: Long): List<RegistroMedicamentoDB> {
        val lista = mutableListOf<RegistroMedicamentoDB>()
        val db = this.safeReadableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())

        val cursor = db.rawQuery("SELECT * FROM $TABLE_MEDICAMENTOS WHERE $COL_MED_USER_ID = ?", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_MED_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_MED_NOMBRE))
                val hora = cursor.getString(cursor.getColumnIndexOrThrow(COL_MED_HORA))
                val dosis = cursor.getString(cursor.getColumnIndexOrThrow(COL_MED_DOSIS))
                val dbTomado = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MED_TOMADO)) == 1
                
                var fechaTomado = ""
                val idxFechaTomado = cursor.getColumnIndex(COL_MED_FECHA_TOMADO)
                if (idxFechaTomado != -1) {
                    fechaTomado = cursor.getString(idxFechaTomado) ?: ""
                }
                
                // Si la columna existe, verificamos la fecha. Si no, o si está vacía, dependemos del valor dbTomado.
                val tomado = if (idxFechaTomado != -1 && fechaTomado.isNotEmpty()) {
                    dbTomado && (fechaTomado == fechaHoy)
                } else {
                    dbTomado
                }
                
                lista.add(RegistroMedicamentoDB(id, nombre, hora, dosis, tomado))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun actualizarMedicamento(id: Long, nombre: String, hora: String, dosis: String) {
        val db = this.safeWritableDatabase
        val values = ContentValues()
        values.put(COL_MED_NOMBRE, nombre)
        values.put(COL_MED_HORA, hora)
        values.put(COL_MED_DOSIS, dosis)
        db.update(TABLE_MEDICAMENTOS, values, "$COL_MED_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun marcarMedicamentoTomado(userId: Long, id: Long, tomado: Boolean) {
        val db = this.safeWritableDatabase
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = sdf.format(Date())
        
        val values = ContentValues()
        values.put(COL_MED_TOMADO, if (tomado) 1 else 0)
        values.put(COL_MED_FECHA_TOMADO, if (tomado) fechaHoy else "")
        db.update(TABLE_MEDICAMENTOS, values, "$COL_MED_ID = ?", arrayOf(id.toString()))
        db.close()
        
        if (tomado) {
            registrarBitacora(userId, "Marcó como tomado el medicamento ID: $id")
        }
    }

    fun eliminarMedicamento(id: Long) {
        val db = this.safeWritableDatabase
        db.delete(TABLE_MEDICAMENTOS, "$COL_MED_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    // --- Métodos de Dashboard ---
    fun obtenerNombre(userId: Long): String {
        var nombre = ""
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_NOMBRE FROM $TABLE_USUARIOS WHERE $COL_USER_ID = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)) ?: ""
        }
        cursor.close()
        db.close()
        return nombre
    }

    fun obtenerUltimoIMC(userId: Long): Double? {
        var ultimoIMC: Double? = null
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_PESO_IMC FROM $TABLE_PESOS WHERE $COL_PESO_USER_ID = ? ORDER BY $COL_PESO_ID DESC LIMIT 1", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            ultimoIMC = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PESO_IMC))
        }
        cursor.close()
        db.close()
        return ultimoIMC
    }

    fun obtenerUltimoPeso(userId: Long): Double? {
        var ultimoPeso: Double? = null
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_PESO_VALOR FROM $TABLE_PESOS WHERE $COL_PESO_USER_ID = ? ORDER BY $COL_PESO_ID DESC LIMIT 1", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            ultimoPeso = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PESO_VALOR))
        }
        cursor.close()
        db.close()
        return ultimoPeso
    }

    fun obtenerUltimaPresion(userId: Long): String? {
        var ultimaPresion: String? = null
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_PRES_SISTOLICA, $COL_PRES_DIASTOLICA FROM $TABLE_PRESIONES WHERE $COL_PRES_USER_ID = ? ORDER BY $COL_PRES_ID DESC LIMIT 1", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            val sistolica = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRES_SISTOLICA))
            val diastolica = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRES_DIASTOLICA))
            ultimaPresion = "$sistolica/$diastolica"
        }
        cursor.close()
        db.close()
        return ultimaPresion
    }

    fun obtenerTotalRegistros(userId: Long): Int {
        val db = this.safeReadableDatabase
        var total = 0
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_BITACORA WHERE $COL_BIT_USER_ID = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return total
    }
    
    // --- Métodos de Pasos ---
    fun guardarPasos(userId: Long, fecha: String, pasos: Int) {
        val db = this.safeWritableDatabase
        val cursor = db.rawQuery("SELECT $COL_PASOS_ID FROM $TABLE_PASOS WHERE $COL_PASOS_USER_ID = ? AND $COL_PASOS_FECHA = ?", arrayOf(userId.toString(), fecha))
        
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PASOS_ID))
            val values = ContentValues()
            values.put(COL_PASOS_CANTIDAD, pasos)
            db.update(TABLE_PASOS, values, "$COL_PASOS_ID = ?", arrayOf(id.toString()))
        } else {
            val values = ContentValues()
            values.put(COL_PASOS_USER_ID, userId)
            values.put(COL_PASOS_FECHA, fecha)
            values.put(COL_PASOS_CANTIDAD, pasos)
            db.insert(TABLE_PASOS, null, values)
        }
        cursor.close()
        db.close()
    }
    
    fun eliminarPasos(userId: Long, fecha: String) {
        val db = this.safeWritableDatabase
        db.delete(TABLE_PASOS, "$COL_PASOS_USER_ID = ? AND $COL_PASOS_FECHA = ?", arrayOf(userId.toString(), fecha))
        db.close()
        registrarBitacora(userId, "Eliminó registro de pasos")
    }

    fun obtenerPasos(userId: Long, fecha: String): Int {
        var pasos = 0
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_PASOS_CANTIDAD FROM $TABLE_PASOS WHERE $COL_PASOS_USER_ID = ? AND $COL_PASOS_FECHA = ?", arrayOf(userId.toString(), fecha))
        if (cursor.moveToFirst()) {
            pasos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PASOS_CANTIDAD))
        }
        cursor.close()
        db.close()
        return pasos
    }
    
    fun obtenerHistorialPasos(userId: Long, dias: Int): Map<String, Int> {
        val historial = mutableMapOf<String, Int>()
        val db = this.safeReadableDatabase
        val cursor = db.rawQuery("SELECT $COL_PASOS_FECHA, $COL_PASOS_CANTIDAD FROM $TABLE_PASOS WHERE $COL_PASOS_USER_ID = ? ORDER BY $COL_PASOS_FECHA DESC LIMIT ?", arrayOf(userId.toString(), dias.toString()))
        
        if (cursor.moveToFirst()) {
            do {
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASOS_FECHA))
                val pasos = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PASOS_CANTIDAD))
                historial[fecha] = pasos
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return historial
    }
}

data class RegistroActividadDB(val id: Long, val nombre: String, val minutos: Int, val intensidad: String)
data class RegistroPesoDB(val id: Long, val fecha: String, val peso: Double, val imc: Double)
data class RegistroPresionDB(val id: Long, val fecha: String, val sistolica: Int, val diastolica: Int, val pulso: Int)
data class RegistroHidratacionDB(val id: Long, val fecha: String, val vasos: Int)
data class RegistroGlucosaDB(val id: Long, val fecha: String, val valor: Int, val tipo: String, val notas: String)
data class RegistroMedicamentoDB(val id: Long, val nombre: String, val hora: String, val dosis: String, var tomado: Boolean)
