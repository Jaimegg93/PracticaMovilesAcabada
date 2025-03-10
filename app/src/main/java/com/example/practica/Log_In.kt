package com.example.practica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Log_In : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login)
        loginButton.setOnClickListener { handleLogin() }
    }

    private fun handleLogin() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Usuario y contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            return
        }

        // Utilizamos el UserDatabaseHelper para validar las credenciales
        val dbHelper = UserDatabaseHelper(this)
        val isValid = dbHelper.checkUserCredentials(username, password)

        if (isValid) {
            // Login correcto, inicia la actividad de Montañas y pasa el usuario actual
            val intent = Intent(this, MontaniasActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
            finish()
        } else {
            // Login incorrecto, muestra un AlertDialog
            AlertDialog.Builder(this)
                .setTitle("Error de Login")
                .setMessage("Usuario o contraseña incorrectos")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
