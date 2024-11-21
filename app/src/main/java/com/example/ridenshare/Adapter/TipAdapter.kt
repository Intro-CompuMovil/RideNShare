package com.example.ridenshare.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.ridenshare.Data.Tip
import com.example.ridenshare.R

class TipAdapter(private val context: Context, private val tips: List<Tip>) : BaseAdapter() {

    override fun getCount(): Int {
        return tips.size
    }

    override fun getItem(position: Int): Any {
        return tips[position]
    }

    override fun getItemId(position: Int): Long {
        return tips[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Inflate the item layout if necessary
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_tip, parent, false)

        // Get the current Tip object
        val currentTip = tips[position]

        // Bind data to views
        val titleTextView = view.findViewById<TextView>(R.id.tip_title)
        val textTextView = view.findViewById<TextView>(R.id.tip_text)

        titleTextView.text = currentTip.titulo
        textTextView.text = currentTip.texto

        return view
    }
}
