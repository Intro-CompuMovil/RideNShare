package com.example.ridenshare.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide // For loading images
import com.example.ridenshare.Data.Producto
import com.example.ridenshare.R

class ProductoAdapter(
    private val context: Context,
    private val productos: List<Producto>
) : BaseAdapter() {

    override fun getCount(): Int = productos.size

    override fun getItem(position: Int): Any = productos[position]

    override fun getItemId(position: Int): Long = productos[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_producto, parent, false
        )

        val producto = getItem(position) as Producto

        // Bind views
        val ivImagen: ImageView = view.findViewById(R.id.ivProductoImagen)
        val tvNombre: TextView = view.findViewById(R.id.tvProductoNombre)
        val tvPrecio: TextView = view.findViewById(R.id.tvProductoPrecio)
        val tvCantidad: TextView = view.findViewById(R.id.tvProductoCantidad)

        // Set data to views
        tvNombre.text = producto.nombre
        tvPrecio.text = "Price: ${producto.precio}"
        tvCantidad.text = "Stock: ${producto.cantidad}"

        // Load image (use a library like Glide or Picasso for better performance)
        Glide.with(context).load(producto.imagen).into(ivImagen)

        return view
    }

}
