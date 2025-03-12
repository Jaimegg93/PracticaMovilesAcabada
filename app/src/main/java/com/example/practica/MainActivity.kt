package com.example.practica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

// Definición de la clase MainActivity que hereda de AppCompatActivity
class MainActivity : AppCompatActivity() {

    // Metodo que se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asocia el diseño con la clase

        // Enlaza la vista de la calculadora y el botón de login con las vistas del XML
        val calculadoraImageView: ImageView = findViewById(R.id.calculadoraImageView) // Imagen que abrirá la calculadora
        val botonImageView: Button = findViewById(R.id.loginImageView) // Botón que abrirá la actividad de login

        // Evento que se dispara cuando se pulsa sobre la imagen de la calculadora
        calculadoraImageView.setOnClickListener {
            val intent = Intent(this, Calculadora::class.java)
            startActivity(intent) // Inicia la nueva actividad
        }

        // Evento que se dispara cuando se pulsa sobre el botón de login
        botonImageView.setOnClickListener {
            val intent = Intent(this, Log_In::class.java)
            startActivity(intent) // Inicia la nueva actividad
        }
    }
}
