package com.ayushcodes.signupandloginusingfirebase

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WelcomeScreen : AppCompatActivity() {
//    private  val binding : ActivityWelcomScreenBinding by lazy {
//        ActivityWelcomScreenBinding.inflate(layoutInflater)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
//        setContentView(binding.root)
        setContentView(R.layout.activity_welcome_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SignInPage::class.java))
            finish()
        },3000)


//        // BELOW IS THE CODE TO EDIT THE TEXT FROM ANY ACTIVITY AS WE WANT...
//        // WE CAN EITHER EDIT TEXT FROM BACKEND, OR WE CAN JUST SIMPLY CREATE A RESOURCE FILE...
//        val welcomeText = "Welcome"
//        val spannableString = SpannableString(welcomeText)
//        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FF0000")),0,5,0)
//        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#312222")),5,welcomeText.length,0)
//        binding.welcomeTextView.text = spannableString
    }
}