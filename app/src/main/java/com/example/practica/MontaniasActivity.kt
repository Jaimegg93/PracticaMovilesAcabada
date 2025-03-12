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

    // Declaración de las variables para las vistas y los datos
    private lateinit var listView: ListView // ListView para mostrar las montañas
    private lateinit var tvCimasConquistadas: TextView // TextView para mostrar el número de cimas conquistadas
    private lateinit var btnAnadirMontana: Button // Botón para añadir una montaña

    // Manejadores de bases de datos para usuarios y montañas
    private lateinit var mountainDB: MountainDatabaseHelper
    private lateinit var userDB: UserDatabaseHelper

    // Datos del usuario actual
    private lateinit var currentUser: String // Nombre del usuario actual
    private var isAdmin: Boolean = false // Si el usuario es admin o no

    // Lista de montañas y adaptador
    private var mountainList = mutableListOf<Mountain>() // Lista de montañas
    private lateinit var adapter: MountainAdapter // Adaptador para mostrar las montañas en el ListView

    // Lista de usuarios disponibles
    private var usuarios = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llama al constructor de la clase padre
        setContentView(R.layout.activity_montanias) // Asocia el diseño XML con la clase

        // Recupera el usuario actual desde el Intent
        currentUser = intent.getStringExtra("username") ?: ""
        isAdmin = currentUser == "admin" // Si el usuario es "admin", se establece isAdmin a true
        userDB = UserDatabaseHelper(this) // Inicializa el manejador de base de datos para usuarios

        // Vincula las vistas con las variables mediante findViewById()
        tvCimasConquistadas = findViewById(R.id.tv_cimas)
        btnAnadirMontana = findViewById(R.id.btn_anadir_montana)
        listView = findViewById(R.id.listView_montanas)

        // Inicializa el manejador de base de datos para montañas
        mountainDB = MountainDatabaseHelper(this)

        // Inicializa el adaptador y lo asocia al ListView
        adapter = MountainAdapter(this, mountainList)
        listView.adapter = adapter

        // Si el usuario no es admin, oculta el botón para añadir montañas
        if (!isAdmin) {
            btnAnadirMontana.visibility = View.GONE
        }

        // Evento para añadir montaña (solo visible para admins)
        btnAnadirMontana.setOnClickListener {
            showAddMountainDialog() // Llama al método para mostrar el cuadro de diálogo para añadir montaña
        }

        // Evento que se dispara al hacer clic en un elemento del ListView
        listView.setOnItemClickListener { _, view, position, _ ->
            showPopupMenuForMountain(view, mountainList[position]) // ✅ Muestra el menú contextual en la montaña seleccionada
        }

        // Carga las montañas y los usuarios en la lista
        loadMountains()
        cargarUsuarios()
    }

    // Metodo para cargar todos los usuarios en el spinner
    private fun cargarUsuarios(){
        usuarios.clear() // Limpia la lista de usuarios
        usuarios.addAll(userDB.getAllUsers()) // Añade los usuarios desde la base de datos
    }

    // Metodo para cargar las montañas en la lista
    private fun loadMountains() {
        mountainList.clear() // Limpia la lista de montañas
        mountainList.addAll(mountainDB.getMountainsForUser(currentUser, isAdmin)) // Carga las montañas desde la base de datos
        adapter.notifyDataSetChanged() // Notifica al adaptador para que actualice el ListView
        tvCimasConquistadas.text = "Nº de cimas conquistadas: ${mountainList.size}" // Muestra el número de montañas
    }

    // Metodo para mostrar el cuadro de diálogo para añadir montaña
    private fun showAddMountainDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_mountain, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.et_nombre)
        val etAltura = dialogView.findViewById<EditText>(R.id.et_altura)
        val etConcejo = dialogView.findViewById<EditText>(R.id.et_concejo)
        val spinnerUsuario = dialogView.findViewById<Spinner>(R.id.spinner_usuario)

        // Carga los usuarios en el spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usuarios)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUsuario.adapter = spinnerAdapter

        // Configura el cuadro de diálogo
        AlertDialog.Builder(this)
            .setTitle("Añadir montaña")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()
                val alturaStr = etAltura.text.toString()
                val usuario = spinnerUsuario.selectedItem.toString()
                val concejo = etConcejo.text.toString()

                if (nombre.isNotEmpty()) {
                    var altura = alturaStr.toIntOrNull()
                    if(altura == null){altura = 0}//si no se

                    if (mountainDB.comporbarMontañaExistente(nombre.lowercase(), usuario)) {
                        Toast.makeText(this, "Esta montaña ya existe para este usuario", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton // Sale del metodo y no inserta
                    }

                    if (altura != null) {
                        val inserted = mountainDB.insertMountain(nombre, usuario, altura, concejo)
                        if (inserted) {
                            Log.d("MontaniasActivity", "Montaña añadida: $nombre")
                            loadMountains() // Actualiza la lista de montañas
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

    private fun showPopupMenuForMountain(view: View, mountain: Mountain) {
        val popup = PopupMenu(this, view) // El menú se ancla a la montaña seleccionada
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
        val tvNombre = dialogView.findViewById<TextView>(R.id.et_nombre) // campo no editable
        val etAltura = dialogView.findViewById<EditText>(R.id.et_altura)
        val etConcejo= dialogView.findViewById<EditText>(R.id.et_concejo)
        val spinnerUsuario = dialogView.findViewById<Spinner>(R.id.spinner_usuario)



        tvNombre.text = mountain.nombre
        etAltura.setText(mountain.altura.toString())
        etConcejo.setText(mountain.concejo)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(mountain.usuario))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUsuario.adapter = spinnerAdapter
        spinnerUsuario.isEnabled= false;

        AlertDialog.Builder(this)
            .setTitle("Modificar montaña")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaAlturaStr = etAltura.text.toString()
                val nuevaAltura = nuevaAlturaStr.toIntOrNull()
                val nuevoConcejo = etConcejo.text.toString()
                if (nuevaAltura != null) {
                    val updated = mountainDB.updateMountain(mountain.nombre, mountain.usuario, nuevaAltura, nuevoConcejo)
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
