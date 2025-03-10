package com.example.practica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.practica.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calculadoraImageView: ImageView = findViewById(R.id.calculadoraImageView)
        val botonImageView: Button = findViewById(R.id.loginImageView)

        calculadoraImageView.setOnClickListener {
            val intent = Intent(this, calculadora::class.java)
            startActivity(intent)
        }
        botonImageView.setOnClickListener {
            val intent = Intent(this, Log_In::class.java)
            startActivity(intent)
        }

    }
}