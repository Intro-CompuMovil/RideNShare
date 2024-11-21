package com.example.ridenshare.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.ridenshare.Logica.TryActivity
import com.example.ridenshare.R

class RutaAdapter(
    private val context: Context,
    private val rutaList: List<String>, // List of Ruta names/keys
    private val onCommentClickListener: (String) -> Unit, // Callback for comment button
    private val onAddComment: (String, String) -> Unit // Callback for adding a comment (Ruta name, Comment)
) : ArrayAdapter<String>(context, 0, rutaList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflate the view if it doesn't exist
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_ruta,
            parent,
            false
        )

        // Get the current Ruta name
        val rutaName = getItem(position)

        // Find views in the layout
        val rutaNameTextView: TextView = view.findViewById(R.id.rutaNameTextView)
        val commentsButton: ImageButton = view.findViewById(R.id.commentsButton)
        val addButton: ImageButton = view.findViewById(R.id.addCommentButton)
        val tryButton: ImageButton = view.findViewById(R.id.tryItButton)

        // Set the Ruta name
        rutaNameTextView.text = rutaName

        // Set click listener for comments button
        commentsButton.setOnClickListener {
            rutaName?.let { name -> onCommentClickListener(name) }
        }

        // Set click listener for add comment button
        addButton.setOnClickListener {
            rutaName?.let { name -> showAddCommentPopup(name) }
        }

        // Set click listener for try button to launch a new activity
        tryButton.setOnClickListener {
            rutaName?.let { name ->
                val intent = Intent(context, TryActivity::class.java).apply {
                    val bundle = Bundle().apply {
                        putString("rutaName", name)  // Put ruta name in the bundle
                    }
                    putExtras(bundle)  // Attach bundle to the intent
                }
                context.startActivity(intent)
            }
        }

        return view
    }


    // Show a popup dialog to add a comment
    private fun showAddCommentPopup(rutaName: String) {
        // Use context directly to create the dialog
        val builder = AlertDialog.Builder(context).apply {
            setTitle("Add Comment")
        }

        // Create an EditText for user input
        val input = EditText(context).apply {
            hint = "Enter your comment"
        }
        builder.setView(input)

        // Set up the dialog buttons
        builder.setPositiveButton("Add") { _, _ ->
            val comment = input.text.toString()
            if (comment.isNotBlank()) {
                // Trigger the callback to add the comment
                onAddComment(rutaName, comment)
            } else {
                Toast.makeText(context, "Comment cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        // Ensure the dialog is shown on the UI thread
    (context as? Activity)?.runOnUiThread {
            builder.show()
        } ?: run {
            builder.show() // Fallback for non-Activity context
        }
    }

}

