package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

import com.google.firebase.database.PropertyName // Imports PropertyName for explicit Firebase mapping

// Defines a data class to hold note information
data class NoteItem(
    val title: String = "",
    val description: String = "",
    val noteID: String = "",
    val date: String = "",
    
    @get:PropertyName("pinned")
    @set:PropertyName("pinned")
    var pinned: Boolean = false // Using "pinned" to avoid "is" prefix issues in Firebase
) 
{
    // The @get/set:PropertyName ensures Firebase always uses the key "pinned" in the database.
}
