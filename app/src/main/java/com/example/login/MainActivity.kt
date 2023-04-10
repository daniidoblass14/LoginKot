package com.example.login

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {

    private var menu: MenuItem? = null
    private var btnRegistrar: Button? = null
    private var btnIniciar: Button? = null
    private var textNombre: TextInputLayout? = null
    private var textPassword: TextInputLayout? = null
    private var nombreEditText: TextInputEditText? = null
    private var passwordEditText: TextInputEditText? = null
    private val pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnRegistrar = findViewById<View>(R.id.Resgistrar) as Button
        btnIniciar = findViewById<View>(R.id.IniciarSesion) as Button
        textNombre = findViewById<View>(R.id.textFieldUsuario) as TextInputLayout
        nombreEditText = textNombre!!.editText as TextInputEditText?
        textPassword = findViewById<View>(R.id.textFieldPassword) as TextInputLayout
        passwordEditText = textPassword!!.editText as TextInputEditText?
        val intentRegistrar = Intent(this, Registrar::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Canal de notificación",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Descripción del canal de notificación"
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        passwordEditText!!.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val nombre = nombreEditText!!.text.toString()
                val password = passwordEditText!!.text.toString()
                iniciarSesion(nombre, password)
                return@OnKeyListener true
            }
            false
        })
        btnRegistrar!!.setOnClickListener {
            startActivity(intentRegistrar)
        }

        btnIniciar!!.setOnClickListener {
            if (nombreEditText!!.text.toString() == "" && passwordEditText!!.text.toString() == "") {
                btnIniciar!!.isEnabled = true
            } else {
                val nombre = nombreEditText!!.text.toString()
                val password = passwordEditText!!.text.toString()
                //iniciarSesion(nombre, password)
                iniciarSesionUrl(nombre,password)
                MyTask().execute(nombre, password)
            }
        }
    }

    private fun iniciarSesionUrl (nombre: String, password: String) {



    }

    private class MyTask : AsyncTask<String?, Int?, LoginResponse>() {
        // Runs in UI before background thread is called

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg p0: String?): LoginResponse {
            TODO("Not yet implemented")
        }

        // This runs in UI when background thread finishes
        override fun onPostExecute(result: LoginResponse) {
            super.onPostExecute(result)

            // Do things like hide the progress bar or change a TextView
        }

    }

    private fun iniciarSesion(nombre: String, password: String) {
        val intentInicio = Intent(this, RecyclerActivity::class.java)
        createNotificacion()

        //Abrimos la base de datos, de forma escritura.
        val acdbhInsert = ActivitySQLiteHelper(this@MainActivity, "users", null, 1)
        val dbInsert: SQLiteDatabase = acdbhInsert.getWritableDatabase()

        // Consulta SQL para buscar registros con el mismo nombre
        val query = "SELECT * FROM users WHERE user = ? AND password = ?"
        val cursor = dbInsert.rawQuery(query, arrayOf(nombre, password))
        if (cursor.count > 0) {
            Toast.makeText(applicationContext, "BIENVENIDO, $nombre", Toast.LENGTH_SHORT).show()
            startActivity(intentInicio)
        } else {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("ERROR")
            builder.setMessage(
                """
                    No se ha encontrado ningún usuario.
                    Intentelo de nuevo
                    """.trimIndent()
            )
            builder.setPositiveButton("Aceptar", null)
            val dialog = builder.create()
            dialog.show()
        }
        nombreEditText!!.setText("")
        passwordEditText!!.setText("")
    }

    private fun createNotificacion() {
        val nombre = nombreEditText!!.text.toString()
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setContentTitle("Inicio de Sesión")
        builder.setContentText("Bienvenido de nuevo, $nombre")
        builder.color = Color.BLUE
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setLights(Color.MAGENTA, 1000, 1000)
        builder.setVibrate(longArrayOf(1000, 1000, 1000, 1000))
        builder.setDefaults(Notification.DEFAULT_SOUND)
        val notificationManagerCompat = NotificationManagerCompat.from(
            applicationContext
        )
        notificationManagerCompat.notify(NOTIFICACION_ID, builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "NOTIFICACION"
        private const val NOTIFICACION_ID = 0
    }
}

