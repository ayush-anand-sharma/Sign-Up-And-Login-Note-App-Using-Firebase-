package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

import android.os.Bundle // Imports the Bundle class for saving instance state
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function for enabling edge-to-edge display
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class for compatibility features
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityReadNoteBinding // Imports the binding class for the activity_read_note.xml layout

class ReadNote : AppCompatActivity() { // Defines the ReadNote activity, which inherits from AppCompatActivity
    private val binding: ActivityReadNoteBinding by lazy { // Lazily initializes the view binding for the activity
        ActivityReadNoteBinding.inflate(layoutInflater) // Inflates the layout for the activity
    }

    override fun onCreate(savedInstanceState: Bundle?) { // Overrides the onCreate method, which is called when the activity is first created
        super.onCreate(savedInstanceState) // Calls the superclass's implementation of onCreate
        enableEdgeToEdge() // Enables edge-to-edge display for the activity
        setContentView(binding.root) // Sets the content view of the activity to the root of the binding

        // Set a click listener for the back button to finish the activity and go back
        binding.backButton.setOnClickListener { 
            finish() // Finishes the current activity
        }

        // Retrieve the note data passed from the previous activity via Intent extras
        val noteTitle = intent.getStringExtra("noteTitle") // Gets the note title from the intent extras
        val noteDescription = intent.getStringExtra("noteDescription") // Gets the note description from the intent extras
        val noteDate = intent.getStringExtra("noteDate") // Gets the note date from the intent extras

        // Set the retrieved data to the respective TextViews in the layout
        binding.titleTextView.text = noteTitle // Sets the text of the title TextView
        binding.descriptionTextView.text = noteDescription // Sets the text of the description TextView
        binding.dateTextView.text = "Date: $noteDate" // Sets the text of the date TextView with a prefix
    }
}