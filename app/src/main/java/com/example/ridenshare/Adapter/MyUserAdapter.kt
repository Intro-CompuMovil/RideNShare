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
import com.example.ridenshare.Data.MyUser
import com.example.ridenshare.Data.Producto
import com.example.ridenshare.Data.UserAdapter
import com.example.ridenshare.R

class MyUserAdapter(
    private val context: Context,
    private val userList: List<UserAdapter>
) : BaseAdapter() {

    override fun getCount(): Int {
        return userList.size
    }

    override fun getItem(position: Int): Any {
        return userList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            // Inflate the item layout
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_amigo, parent, false)

            // Initialize the view holder
            holder = ViewHolder(
                view.findViewById(R.id.amigo_nombre),
                view.findViewById(R.id.amigo_correo),
                view.findViewById(R.id.amigo_ciudad)
            )

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        // Get the current MyUser object
        val currentUser = getItem(position) as UserAdapter

        // Populate the views with data
        holder.nombreTextView.text = currentUser.apodo
        holder.correoTextView.text = currentUser.correo
        holder.ciudadTextView.text = currentUser.ciudad

        return view
    }

    // ViewHolder class to optimize view lookup
    private class ViewHolder(
        val nombreTextView: TextView,
        val correoTextView: TextView,
        val ciudadTextView: TextView
    )
}
