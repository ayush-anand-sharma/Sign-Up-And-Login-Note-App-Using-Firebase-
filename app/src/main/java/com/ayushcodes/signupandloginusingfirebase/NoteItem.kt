package com.ayushcodes.signupandloginusingfirebase

data class NoteItem(val title: String, val description: String, val noteID: String)
{
    constructor() : this("", "","")
}
