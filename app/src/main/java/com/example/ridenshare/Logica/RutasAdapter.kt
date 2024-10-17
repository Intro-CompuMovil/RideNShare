package com.example.ridenshare.Logica

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ridenshare.R

class RutasAdapter(
    private val rutas: List<Ruta>,
    private val onItemClick: ((Ruta) -> Unit)? = null // Parámetro opcional con un valor por defecto
) : RecyclerView.Adapter<RutasAdapter.RutaViewHolder>() {

    inner class RutaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.nombreRuta)
        val distanciaTextView: TextView = view.findViewById(R.id.distanciaRuta)
        val tiempoTextView: TextView = view.findViewById(R.id.tiempoRuta)

        fun bind(ruta: Ruta) {
            nombreTextView.text = ruta.nombre
            distanciaTextView.text = "Distancia: ${String.format("%.2f", ruta.distancia)} km"
            tiempoTextView.text = "Tiempo: ${ruta.tiempo}"

            // Configura el listener solo si onItemClick no es null
            itemView.setOnClickListener {
                onItemClick?.invoke(ruta)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ruta, parent, false)
        return RutaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RutaViewHolder, position: Int) {
        val ruta = rutas[position]
        holder.bind(ruta)
    }

    override fun getItemCount(): Int = rutas.size
}
