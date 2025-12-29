package com.ayushcodes.signupandloginusingfirebase

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityAddNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddNote : AppCompatActivity() {
    private val binding: ActivityAddNoteBinding by lazy {
        ActivityAddNoteBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        binding.saveNoteButton.setOnClickListener {

            val title = binding.editTextTitle.text.toString()
            val description = binding.editTextDescription.text.toString()

            if (title.isEmpty() && description.isEmpty()) {
                Toast.makeText(this, "Please Enter Your Title and Description", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val currentUser = auth.currentUser
                currentUser?.let { user ->
                    val notekey: String? =
                        databaseReference.child("users").child(user.uid).child("notes").push().key

                    val noteItem = NoteItem(title, description, notekey?:"")

                    if (notekey != null) {
                        databaseReference.child("users").child(user.uid).child("notes")
                            .child(notekey).setValue(noteItem)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Note Save Successful", Toast.LENGTH_SHORT)
                                        .show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed To Save Note", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                    }
                }
            }
        }
    }
}