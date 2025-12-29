package com.ayushcodes.signupandloginusingfirebase

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayushcodes.signupandloginusingfirebase.databinding.ActivityAllNotesBinding
import com.ayushcodes.signupandloginusingfirebase.databinding.DialogUpdateNotesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllNotes : AppCompatActivity(), NoteAdapter.OnitemClickListener {
    private val binding: ActivityAllNotesBinding by lazy {
        ActivityAllNotesBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private var noteListener: ValueEventListener? = null
    private var noteReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        recyclerView = binding.noteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Initialize adapter with empty list
        noteAdapter = NoteAdapter(emptyList(), this)
        recyclerView.adapter = noteAdapter

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            noteReference = databaseReference.child("users").child(user.uid).child("notes")
            
            noteListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val noteList = mutableListOf<NoteItem>()
                    for (noteSnapshot in snapshot.children) {
                        val note = noteSnapshot.getValue(NoteItem::class.java)
                        note?.let {
                            noteList.add(it)
                        }
                    }
                    noteList.reverse()
                    noteAdapter.updateData(noteList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AllNotes, "Failed to load notes: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
            noteReference?.addValueEventListener(noteListener!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        noteListener?.let {
            noteReference?.removeEventListener(it)
        }
    }

    override fun onDeleteClick(noteID: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReference = databaseReference.child("users").child(user.uid).child("notes")
            noteReference.child(noteID).removeValue()
        }
    }

    override fun onUpdateClick(noteID: String, title: String, currentDescription: String) {
        val dialogBinding: DialogUpdateNotesBinding = DialogUpdateNotesBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root)
            .setTitle("Update Note")
            .setPositiveButton("Update") { dialog, _ ->
                val newTitle = dialogBinding.updateNoteTitle.text.toString()
                val newDescription = dialogBinding.updateNoteDescription.text.toString()
                updateNoteDatabase(noteID, newTitle, newDescription)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialogBinding.updateNoteTitle.setText(title)
        dialogBinding.updateNoteDescription.setText(currentDescription)
        dialog.show()
    }

    private fun updateNoteDatabase(noteID: String, newTitle: String, newDescription: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val noteReference = databaseReference.child("users").child(user.uid).child("notes")
            val updatedNote = NoteItem(newTitle, newDescription, noteID)
            noteReference.child(noteID).setValue(updatedNote)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}