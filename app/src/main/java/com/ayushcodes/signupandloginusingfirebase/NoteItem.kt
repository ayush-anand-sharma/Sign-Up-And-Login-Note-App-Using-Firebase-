package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

import com.google.firebase.database.PropertyName // Imports the PropertyName annotation for explicit mapping of fields in the Firebase Realtime Database

/**
 * Defines a data class to hold individual note information.
 * Data classes in Kotlin are primarily used to hold data and automatically provide useful methods like equals(), hashCode(), and toString().
 */
data class NoteItem( // Starts the definition of the NoteItem data class
    val title: String = "", // Declares a title property of type String with a default empty value
    val description: String = "", // Declares a description property of type String with a default empty value
    val noteID: String = "", // Declares a unique identifier for the note of type String with a default empty value
    val date: String = "", // Declares a date property of type String to store when the note was created or modified
    
    @get:PropertyName("pinned") // Tells Firebase to use the key "pinned" when reading this property's value from the database
    @set:PropertyName("pinned") // Tells Firebase to use the key "pinned" when writing this property's value to the database
    var pinned: Boolean = false // Declares a mutable boolean property to indicate if a note is pinned, defaulting to false
) // Ends the primary constructor of the data class
{ // Starts the body of the class (if needed for additional logic)
    // This empty body is used as a placeholder; the @get/set:PropertyName ensures Firebase always uses the key "pinned" in the database.
} // Ends the definition of the NoteItem class
