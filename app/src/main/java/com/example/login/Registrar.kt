package com.example.login

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Registrar : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var imagePath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)

        val textFieldUsuario = findViewById<TextInputLayout>(R.id.textFieldUsuario)
        val textFieldPassword = findViewById<TextInputLayout>(R.id.textFieldPassword)
        val textFieldFecha = findViewById<TextInputLayout>(R.id.textFieldFecha)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnCamara = findViewById<Button>(R.id.btnCamara)
        val btnGaleria = findViewById<Button>(R.id.btnGaleria)
        val imageView = findViewById<ImageView>(R.id.imageView)

        val nombre = textFieldUsuario.editText?.text.toString()
        val password = textFieldPassword.editText?.text.toString()

        val materialDateBuilder = MaterialDatePicker.Builder.datePicker()
        val materialDatePicker = materialDateBuilder.build()

        textFieldFecha.editText?.setOnClickListener {

            if(!materialDatePicker.isAdded){
                materialDatePicker.show(supportFragmentManager,"tag")
            }
        }

        materialDatePicker.addOnPositiveButtonClickListener {
            textFieldFecha.editText?.setText(materialDatePicker.headerText)

        }

        btnCamara.setOnClickListener {

            if(ContextCompat.checkSelfPermission(this@Registrar, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@Registrar, arrayOf(android.Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
            }else{
                dispatchTakePictureIntent(imageUri)
            }
        }

    }

    companion object {
        val REQUEST_PICK_IMAGE = 2
        val REQUEST_PERMISSION_CAMERA = 1001
        val REQUEST_IMAGE_CAPTURE: Int = 1
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
                startActivityForResult(takePictureIntent, Registrar.REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File? {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName,".jpg",storageDir)

        imagePath = image.absolutePath
        return image
    }


}