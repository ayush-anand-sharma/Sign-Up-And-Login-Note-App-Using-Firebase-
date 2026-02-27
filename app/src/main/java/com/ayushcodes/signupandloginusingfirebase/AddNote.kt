package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

import android.content.Context
import android.os.Bundle // Imports the Bundle class for saving instance state
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast // Imports the Toast class for showing short messages
import androidx.activity.enableEdgeToEdge // Imports the enableEdgeToEdge function for enabling edge-to-edge display
import androidx.appcompat.app.AppCompatActivity // Imports the AppCompatActivity class for compatibility features
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Imports the installSplashScreen function for creating a splash screen
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityAddNoteBinding // Imports the binding class for the activity_add_note.xml layout
import com.google.firebase.auth.FirebaseAuth // Imports the FirebaseAuth class for user authentication
import com.google.firebase.database.DatabaseReference // Imports the DatabaseReference class for interacting with the Firebase Realtime Database
import com.google.firebase.database.FirebaseDatabase // Imports the FirebaseDatabase class for accessing the Firebase Realtime Database
import java.text.SimpleDateFormat // Imports the SimpleDateFormat class for formatting dates
import java.util.Date // Imports the Date class for representing a specific instant in time
import java.util.Locale // Imports the Locale class for representing a specific geographical, political, or cultural region

class AddNote : AppCompatActivity() { // Defines the AddNote activity, which inherits from AppCompatActivity
    private val binding: ActivityAddNoteBinding by lazy { // Lazily initializes the view binding for the activity
        ActivityAddNoteBinding.inflate(layoutInflater) // Inflates the layout for the activity
    }

    private lateinit var databaseReference: DatabaseReference // Declares a lateinit variable for the Firebase Realtime Database reference
    private lateinit var auth: FirebaseAuth // Declares a lateinit variable for the Firebase authentication instance

    override fun onCreate(savedInstanceState: Bundle?) { // Overrides the onCreate method, which is called when the activity is first created
        super.onCreate(savedInstanceState) // Calls the superclass's implementation of onCreate
        installSplashScreen() // Installs a splash screen for the activity
        enableEdgeToEdge() // Enables edge-to-edge display for the activity
        setContentView(binding.root) // Sets the content view of the activity to the root of the binding

        databaseReference = FirebaseDatabase.getInstance().reference // Initializes the database reference to the root of the Firebase Realtime Database
        auth = FirebaseAuth.getInstance() // Initializes the FirebaseAuth instance

        binding.backButton.setOnClickListener { // Sets an OnClickListener for the back button
            finish() // Finishes the activity
        }

        binding.saveNoteButton.setOnClickListener { // Sets an OnClickListener for the save note button

            val title = binding.editTextTitle.text.toString() // Gets the text from the title EditText and converts it to a string
            val description = binding.editTextDescription.text.toString() // Gets the text from the description EditText and converts it to a string
            val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) // Gets the current date and formats it as "dd-MM-yyyy"

            if (title.isEmpty() && description.isEmpty()) { // Checks if both the title and description are empty
                Toast.makeText(this, "Please Enter Your Title and Description", Toast.LENGTH_SHORT) // Shows a toast message asking the user to enter a title and description
                    .show() // Displays the toast message
            } else { // If either the title or description is not empty
                val currentUser = auth.currentUser // Gets the currently signed-in user
                currentUser?.let { user -> // If a user is signed in
                    showProgressBar(true) // Show the progress bar before starting the save process.
                    // Generate a unique key for the new note.
                    val noteKey = databaseReference.child("Users").child(user.uid).child("Notes").push().key

                    if (noteKey != null) { // If a unique key was successfully generated
                        // Create a new NoteItem with the title, description, the generated key, and date.
                        val noteItem = NoteItem(title, description, noteKey, date)

                        // Get a reference to the new note's location in the database.
                        databaseReference.child("Users").child(user.uid).child("Notes").child(noteKey)
                            .setValue(noteItem) // Set the value of the new note to the NoteItem object.
                            .addOnCompleteListener { task -> // Adds a listener that is called when the operation is complete
                                showProgressBar(false) // Hide the progress bar once the save operation is complete.
                                if (task.isSuccessful) { // If the note was saved successfully
                                    Toast.makeText(this, "Note Saved Successfully", Toast.LENGTH_SHORT).show() // Shows a toast message indicating success.
                                    finish() // Finishes the activity.
                                } else { // If the note failed to save
                                    Toast.makeText(this, "Failed To Save Note", Toast.LENGTH_SHORT).show() // Shows a toast message indicating failure.
                                }
                            }
                    } else {
                        showProgressBar(false) // Hide the progress bar if key generation fails.
                        // If for some reason the key is null, inform the user.
                        Toast.makeText(this, "Failed to generate a unique ID for the note.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // This function shows or hides the progress bar and dim overlay.
    private fun showProgressBar(show: Boolean) { // Defines a function to show or hide the progress bar.
        if (show) { // If the `show` parameter is true,
            binding.progressBar.visibility = View.VISIBLE // Makes the progress bar visible.
            binding.dimOverlay.visibility = View.VISIBLE // Makes the dim overlay visible.
            binding.saveNoteButton.isEnabled = false // Disables the save note button to prevent multiple clicks.
            hideKeyboard() // Hides the keyboard.
        } else { // If the `show` parameter is false,
            binding.progressBar.visibility = View.GONE // Hides the progress bar.
            binding.dimOverlay.visibility = View.GONE // Hides the dim overlay.
            binding.saveNoteButton.isEnabled = true // Enables the save note button again.
        }
    }

    // This function hides the soft keyboard.
    private fun hideKeyboard() { // Defines a function to hide the keyboard.
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // Gets an instance of the InputMethodManager.
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0) // Hides the keyboard from the currently focused window.
    }
}