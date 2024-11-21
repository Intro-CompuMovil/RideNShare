package com.example.ridenshare.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.ridenshare.Data.Publicacion
import com.example.ridenshare.R

class PublicacionAdapter(
    private val context: Context,
    private val publicaciones: List<Publicacion>
) : BaseAdapter() {

    override fun getCount(): Int = publicaciones.size

    override fun getItem(position: Int): Any = publicaciones[position]

    override fun getItemId(position: Int): Long = publicaciones[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_publicacion, parent, false
        )

        val publicacion = getItem(position) as Publicacion

        val ivImagen = view.findViewById<ImageView>(R.id.ivImagen)
        val tvTitulo = view.findViewById<TextView>(R.id.tvTitulo)
        val tvDescripcion = view.findViewById<TextView>(R.id.tvDescripcion)
        val tvFechaHora = view.findViewById<TextView>(R.id.tvFechaHora)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)

        tvTitulo.text = publicacion.titulo
        tvDescripcion.text = publicacion.descripcion
        tvFechaHora.text = "${publicacion.fecha} ${publicacion.hora}"
        tvUserName.text = publicacion.userName

        // Load the image (use a library like Glide or Picasso)
        Glide.with(context)
            .load(publicacion.imagen) // URL or URI of the image
            .into(ivImagen)

        return view
    }
}
