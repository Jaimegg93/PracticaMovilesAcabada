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

    // Declaración de las variables para las vistas
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    // Metodo que se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        // Enlaza las vistas con las variables mediante findViewById()
        usernameEditText = findViewById(R.id.username) // Asocia el campo de texto de usuario
        passwordEditText = findViewById(R.id.password) // Asocia el campo de texto de contraseña
        loginButton = findViewById(R.id.login) // Asocia el botón de login

        // Evento que se dispara al pulsar el botón de inicio de sesión
        loginButton.setOnClickListener {
            handleLogin() // Llama a la función que gestiona el inicio de sesión
        }
    }

    // Metodo para gestionar el inicio de sesión
    private fun handleLogin() {
        val username = usernameEditText.text.toString() // Obtiene el texto introducido en el campo de usuario
        val password = passwordEditText.text.toString() // Obtiene el texto introducido en el campo de contraseña

        // Si el usuario o la contraseña están vacíos, muestra un mensaje de error
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Usuario y contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            return // Sale del método para evitar que continúe
        }

        // Crea una instancia del manejador de base de datos
        val dbHelper = UserDatabaseHelper(this)
        val isValid = dbHelper.checkUserCredentials(username, password) // Comprueba las credenciales

        if (isValid) {
            // Si las credenciales son correctas, abre la actividad MontaniasActivity
            val intent = Intent(this, MontaniasActivity::class.java)
            intent.putExtra("username", username) // Pasa el nombre de usuario a la nueva actividad
            startActivity(intent) // Inicia la nueva actividad
            finish() // Cierra la actividad actual para evitar volver atrás
        } else {
            // Si las credenciales son incorrectas, muestra un cuadro de diálogo con el error
            AlertDialog.Builder(this)
                .setTitle("Error de Login")
                .setMessage("Usuario o contraseña incorrectos")
                .setPositiveButton("OK", null) // Añade un botón OK para cerrar el cuadro de diálogo
                .show()
        }
    }
}