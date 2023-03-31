package com.example.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.login.ActivitySQLiteHelper
import com.example.login.R
import com.example.login.RecyclerActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class Registrar : AppCompatActivity(), View.OnClickListener {


    private var btnCamara: Button? = null
    private var btnGaleria: Button? = null
    private var btnRegistrar: Button? = null

    private var imageView: ImageView? = null
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    private var nombre_recuperado: String? = null
    private var fecha_recuperado: String? = null
    private var imagen_recuperada: Bitmap? = null
    private var mediaPlayer: MediaPlayer? = null

    private var textNombre: TextInputLayout? = null
    private var textPassword: TextInputLayout? = null
    private var textFecha: TextInputLayout? = null
    private var fechaEditText:TextInputEditText? = null
    private var nombreEditText: TextInputEditText? = null
    private var passwordEditText: TextInputEditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.song)
        btnCamara = findViewById<View>(R.id.btnCamara) as Button
        btnGaleria = findViewById<View>(R.id.btnGaleria) as Button
        btnRegistrar = findViewById<View>(R.id.btnRegistrar) as Button

        textNombre = findViewById<View>(R.id.textFieldUsuario) as TextInputLayout
        nombreEditText = textNombre!!.editText as TextInputEditText?

        textPassword = findViewById<View>(R.id.textFieldPassword) as TextInputLayout
        passwordEditText = textPassword!!.editText as TextInputEditText?

        textFecha = findViewById<View>(R.id.textFieldFecha) as TextInputLayout
        fechaEditText = textFecha!!.editText as TextInputEditText?

        btnRegistrar!!.setOnClickListener(this)
        val datos = this.intent.extras
        if (datos != null) {
            nombre_recuperado = datos.getString("nombre")
            fecha_recuperado = datos.getString("fecha")

        }
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
            MaterialDatePicker.Builder.datePicker()
        val materialDatePicker = materialDateBuilder.build()
        fechaEditText?.setOnClickListener {
            if (!materialDatePicker.isAdded) {
                materialDatePicker.show(supportFragmentManager, "tag")
            }
        }
        materialDatePicker.addOnPositiveButtonClickListener {
            fechaEditText?.setText(
                materialDatePicker.headerText
            )
        }
        imageView = findViewById(R.id.imageView)
        btnCamara!!.setOnClickListener(View.OnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@Registrar,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@Registrar,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSION_CAMERA
                )
            } else {
                dispatchTakePictureIntent()
            }
        })
        btnGaleria!!.setOnClickListener(View.OnClickListener {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE)
        })


    }

    @SuppressLint("Range")
    override fun onClick(v: View) {
        if (v.id == R.id.btnRegistrar) {
            mediaPlayer!!.start()
            if (this@Registrar.intent.hasExtra("nombre")) {

                //Abrimos la base de datos, de forma escritura.
                val acdbhComprobar = ActivitySQLiteHelper(this@Registrar, "users", null, 1)
                val dbComprobar: SQLiteDatabase = acdbhComprobar.writableDatabase

                // Obtener el nombre del usuario
                val nombre_recuperado = intent.getStringExtra("nombre")

                // Consulta SQL para buscar registros con el mismo nombre
                val query = "SELECT * FROM users WHERE user = ?"
                val cursor = dbComprobar.rawQuery(query, arrayOf(nombre_recuperado))
                if (cursor.count > 0) {
                    // Ya existe un registro con el mismo nombre
                    Log.d("MainActivity", "Ya existe un registro con el mismo nombre")

                    val acdbhConsulta = ActivitySQLiteHelper(this@Registrar, "users", null, 1)
                    val dbConsulta: SQLiteDatabase = acdbhConsulta.readableDatabase

                    val cursorConsulta = dbConsulta.rawQuery(query, arrayOf(nombre_recuperado))

                    if (cursorConsulta.moveToFirst()) {
                        val user = cursorConsulta.getString(cursorConsulta.getColumnIndex("user"))
                        val date = cursorConsulta.getString(cursorConsulta.getColumnIndex("date"))
                        val imageBytes = cursorConsulta.getBlob(cursorConsulta.getColumnIndex("photo"))
                        val password = cursorConsulta.getString(cursorConsulta.getColumnIndex("password"))

                        textNombre?.editText?.setText(user)
                        textFecha?.editText?.setText(date)
                        textPassword?.editText?.setText(password)

                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageView?.setImageBitmap(bitmap)
                    }

                    cursorConsulta.close()
                    dbConsulta.close()

                    // Actualizar datos en la base de datos
                    var inputStream: InputStream? = null
                    try {
                        inputStream = contentResolver.openInputStream(imageUri!!)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    val bytes = getBytes(inputStream)
                    val values = ContentValues()
                    values.put("user", nombreEditText!!.text.toString())
                    values.put("password", passwordEditText!!.text.toString())
                    values.put("date", fechaEditText!!.text.toString())
                    values.put("photo", bytes)
                    val cantidadActualizada = dbComprobar.update("users", values, "user = ?", arrayOf(nombre_recuperado))
                    if (cantidadActualizada > 0) {
                        Toast.makeText(applicationContext, "Se ha actualizado!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@Registrar, RecyclerActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "No se ha actualizado!", Toast.LENGTH_SHORT).show()
                    }
                    cursor.close()
                }
                dbComprobar.close()
            }


            val nombre = nombreEditText!!.text.toString()

            //Abrimos la base de datos, de forma escritura.
            val acdbhInsert = ActivitySQLiteHelper(this@Registrar, "users", null, 1)
            val dbInsert: SQLiteDatabase = acdbhInsert.getWritableDatabase()

            // Consulta SQL para buscar registros con el mismo nombre
            val consulta = "SELECT * FROM users WHERE user = ?"
            val cursorConsulta = dbInsert.rawQuery(consulta, arrayOf(nombre))
            if (cursorConsulta.count > 0) {
                // Ya existe un registro con el mismo nombre, mostrar toast
                Toast.makeText(
                    applicationContext,
                    "Ya existe un usuario con ese nombre",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // No existe un registro con el mismo nombre, insertar en la base de datos
                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(imageUri!!)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                val bytes = getBytes(inputStream)
                val values = ContentValues()
                values.put("user", nombreEditText!!.text.toString())
                values.put("password", passwordEditText!!.text.toString())
                values.put("date",fechaEditText!!.text.toString())
                values.put("photo", bytes)
                val result = dbInsert.insert("users", null, values)
                Toast.makeText(
                    applicationContext,
                    "Se ha registrado con existo!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            cursorConsulta.close()
            dbInsert.close()
            finish()
        }
    }

    private fun getBytes(inputStream: InputStream?): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (true) {
            try {
                if (inputStream!!.read(buffer).also { len = it } == -1) break
            } catch (e: IOException) {
                e.printStackTrace()
            }
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                ex.printStackTrace()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(
                    this,
                    "com.example.prueba.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        imagePath = image.absolutePath
        return image
    }

    private fun dispatchPickPictureIntent() {
        val pickPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPictureIntent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Show the photo on the ImageView
                setPic()
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                // Get the Uri of the selected image
                imageUri = data!!.data
                // Set the image on the ImageView
                imageView!!.setImageURI(imageUri)
            }
        }
    }

    private fun setPic() {

        // Establece el tamaño de destino de la imagen
        val targetW = 1000
        val targetH = 1000

        // Obtiene las dimensiones de la imagen original
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Calcula el factor de escala para que la imagen se ajuste al tamaño de destino
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decodifica la imagen original con el factor de escala calculado
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true
        val bitmap = BitmapFactory.decodeFile(imagePath, bmOptions)
        imageView!!.setImageBitmap(bitmap)
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Advertencia")
        builder.setMessage("¿Desea volver hacia atrás?")
        builder.setPositiveButton(
            "Aceptar"
        ) { dialogInterface, i -> salir() }
        builder.setNegativeButton("Cancelar", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun salir() {
        super.onBackPressed()
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PICK_IMAGE = 2
        private const val REQUEST_PERMISSION_CAMERA = 1001
    }
}

