📝 SignUp and Login Note App (Firebase)

A secure and real-time Android Note-Taking application built with Kotlin and Firebase. This app features a robust authentication system and allows users to create, read, update, and delete (CRUD) notes that are stored securely in the cloud.

🚀 Features

🔐 Authentication

• Sign Up: Create a new account using Email, Username, and Password. Includes validation (e.g., password matching).

• Sign In: Secure login with Email and Password.

• Auto Login: The app remembers the logged-in user and skips the login screen on subsequent launches.

• Sign Out: Securely log out from the main dashboard.

• Splash Screen: Integrated core-splashscreen API for a smooth startup experience.


📒 Note Management (CRUD)

• Create Notes: Add new notes with a Title and Description.

• Read Notes: View a list of all your saved notes in a clean RecyclerView interface.

• Update Notes: Edit existing notes.

• Delete Notes: Remove unwanted notes directly from the list.

• Real-time Sync: All notes are synced instantly across devices using the Firebase Realtime Database.


🎨 UI/UX

• Material Design: Clean and intuitive user interface.

• Custom Styling: Uses custom fonts (Kurale) and drawable resources for a unique look.

• Feedback: Toast messages provide instant feedback for user actions (success/failure).



🛠️ Tech Stack

• Language: Kotlin

• Platform: Android

• Backend: Firebase
        ◦ Firebase Authentication: For user management.
        ◦ Firebase Realtime Database: For storing and syncing notes.

• Architecture: Activity-based with ViewBinding.

• Key Android Libraries:
        ◦ androidx.recyclerview: For efficient list display.
        ◦ androidx.constraintlayout: For responsive layouts.
        ◦ androidx.core-splashscreen: For the splash screen.
        ◦ ViewBinding: For interacting with views safely.

🔑 Firebase Setup

SHA-1: 1D:9B:A2:FF:CA:D8:7E:54:D9:72:4D:69:CD:93:A5:C2:CC:27:37:C5

SHA-256: 37:D0:40:76:FF:F9:D7:B4:BD:6B:55:44:C4:35:51:A3:2E:C2:44:AA:9A:BD:CE:44:32:3A:CA:E3:6E:F9:4D:9F
