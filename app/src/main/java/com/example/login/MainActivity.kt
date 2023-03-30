package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textFieldUsuario = findViewById<TextInputLayout>(R.id.textFieldUsuario)
        val textFieldPassword = findViewById<TextInputLayout>(R.id.textFieldPassword)
        val btnIniciarSesion = findViewById<Button>(R.id.IniciarSesion)
        val btnRegistrar = findViewById<Button>(R.id.Resgistrar)

        val intentRegistrar = Intent(this,Registrar::class.java)

        btnRegistrar.setOnClickListener {


        }

    }
}