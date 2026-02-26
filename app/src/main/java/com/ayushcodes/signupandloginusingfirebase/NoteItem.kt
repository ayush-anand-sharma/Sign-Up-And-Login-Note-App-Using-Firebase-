package com.ayushcodes.signupandloginusingfirebase // Declares the package name for the class

data class NoteItem(val title: String, val description: String, val noteID: String, val date: String, var isPinned: Boolean = false) // Defines a data class to hold note information
{
    constructor() : this("", "", "", "", false) // Provides a default constructor for Firebase
}
