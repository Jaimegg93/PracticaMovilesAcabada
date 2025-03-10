package com.example.practica

import android.app.AlertDialog
import android.os.Bundle
import android.os.UserHandle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.practica.R

class MontaniasActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var tvCimasConquistadas: TextView
    private lateinit var btnAnadirMontana: Button

    private lateinit var mountainDB: MountainDatabaseHelper
    private lateinit var userDB: UserDatabaseHelper

    private lateinit var currentUser: String
    private var isAdmin: Boolean = false
    private var mountainList = mutableListOf<Mountain>()
    private lateinit var adapter: MountainAdapter
    private var usuarios = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_montanias)

        // Recupera el usuario actual (enviado desde el login)
        currentUser = intent.getStringExtra("username") ?: ""
        isAdmin = currentUser == "admin"
        userDB = UserDatabaseHelper(this)

        tvCimasConquistadas = findViewById(R.id.tv_cimas)
        btnAnadirMontana = findViewById(R.id.btn_anadir_montana)
        listView = findViewById(R.id.listView_montanas)

        mountainDB = MountainDatabaseHelper(this)
        adapter = MountainAdapter(this, mountainList)
        listView.adapter = adapter

        // Si el usuario no es admin, ocultamos el botón de añadir
        if (!isAdmin) {
            btnAnadirMontana.visibility = View.GONE
        }

        btnAnadirMontana.setOnClickListener {
            showAddMountainDialog()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            showPopupMenuForMountain(mountainList[position])
        }

        loadMountains()
        cargarUsuarios()
    }
private fun cargarUsuarios(){
    usuarios.clear()
    usuarios.addAll(userDB.getAllUsers())
}
    private fun loadMountains() {
        mountainList.clear()
        mountainList.addAll(mountainDB.getMountainsForUser(currentUser, isAdmin))
        adapter.notifyDataSetChanged()
        tvCimasConquistadas.text = "Nº de cimas conquistadas: ${mountainList.size}"
    }

    private fun showAddMountainDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_mountain, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.et_nombre)
        val etAltura = dialogView.findViewById<EditText>(R.id.et_altura)
        val spinnerUsuario = dialogView.findViewById<Spinner>(R.id.spinner_usuario)
        // Carga el spinner con los dos usuarios disponibles
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usuarios)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUsuario.adapter = spinnerAdapter


        AlertDialog.Builder(this)
            .setTitle("Añadir montaña")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()
                val alturaStr = etAltura.text.toString()
                val usuario = spinnerUsuario.selectedItem.toString()
                if (nombre.isNotEmpty() && alturaStr.isNotEmpty()) {
                    val altura = alturaStr.toIntOrNull()
                    if (altura != null) {
                        val inserted = mountainDB.insertMountain(nombre, usuario, altura)
                        if (inserted) {
                            Log.d("MontaniasActivity", "Montaña añadida: $nombre")
                            loadMountains()
                        } else {
                            Toast.makeText(this, "Error al añadir montaña", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Altura inválida", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create().show()
    }

    private fun showPopupMenuForMountain(mountain: Mountain) {
        val popup = PopupMenu(this, listView)
        popup.menuInflater.inflate(R.menu.menu_mountain, popup.menu)
        if (!isAdmin) {
            popup.menu.removeItem(R.id.menu_eliminar)
        }
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_modificar -> {
                    showModifyMountainDialog(mountain)
                    true
                }

                R.id.menu_eliminar -> {
                        showDeleteMountainDialog(mountain)
                        true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun showModifyMountainDialog(mountain: Mountain) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_modify_mountain, null)
        val tvNombre = dialogView.findViewById<TextView>(R.id.tv_nombre) // campo no editable
        val etAltura = dialogView.findViewById<EditText>(R.id.et_altura)
        tvNombre.text = mountain.nombre
        etAltura.setText(mountain.altura.toString())

        AlertDialog.Builder(this)
            .setTitle("Modificar montaña")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaAlturaStr = etAltura.text.toString()
                val nuevaAltura = nuevaAlturaStr.toIntOrNull()
                if (nuevaAltura != null) {
                    val updated = mountainDB.updateMountain(mountain.nombre, mountain.usuario, nuevaAltura)
                    if (updated) {
                        Log.d("MontaniasActivity", "Montaña modificada: ${mountain.nombre}")
                        loadMountains()
                    } else {
                        Toast.makeText(this, "Error al modificar montaña", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Altura inválida", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create().show()
    }

    private fun showDeleteMountainDialog(mountain: Mountain) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar montaña")
            .setMessage("¿Estás seguro de que quieres eliminar la montaña?")
            .setPositiveButton("Sí") { _, _ ->
                val deleted = mountainDB.deleteMountain(mountain.nombre, mountain.usuario)
                if (deleted) {
                    Log.d("MontaniasActivity", "Montaña eliminada: ${mountain.nombre}")
                    loadMountains()
                } else {
                    Toast.makeText(this, "Error al eliminar montaña", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .create().show()
    }
}
