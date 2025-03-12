package com.example.practica

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.practica.R

class MountainAdapter(private val context: Context, private val mountains: List<Mountain>) : BaseAdapter() {
    override fun getCount(): Int = mountains.size

    override fun getItem(position: Int): Any = mountains[position]

    override fun getItemId(position: Int): Long = position.toLong()

    //  Metodo clave → Genera la vista para cada elemento de la lista
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_mountain, parent, false)
        val mountain = mountains[position]
        val ivIcon = view.findViewById<ImageView>(R.id.iv_mountain_icon)
        val tvInfo = view.findViewById<TextView>(R.id.tv_mountain_info)
        val tvConcejo = view.findViewById<TextView>(R.id.tv_mountain_concejo)


        val nombreSinEspacions = mountain.nombre.lowercase().replace(" ", "")  // Elimina todos los espacios

        // Cargar la imagen a partir del nombre de la montaña; si no existe, usar una imagen por defecto
        val resourceId = context.resources.getIdentifier(nombreSinEspacions, "drawable", context.packageName)
        if (resourceId != 0) {
            ivIcon.setImageResource(resourceId)
        } else {
            ivIcon.setImageResource(R.drawable.default_mountain_icon)
        }

        if(mountain.concejo.length ==0){
            tvConcejo.text = "Concejo desconocido"
        }else{
            tvConcejo.text = "${mountain.concejo}"
        }

        if( mountain.altura == 0) {
            tvInfo.text = "${mountain.nombre} - Altura no definida "
        }
        else{
            tvInfo.text = "${mountain.nombre} - ${mountain.altura} metros "
        }

        return view
    }
}
