package com.pa.submissionaplikasistoryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import com.pa.submissionaplikasistoryapp.data.remote.pref.UserTokenPref
import com.pa.submissionaplikasistoryapp.ui.HomeActivity
import com.pa.submissionaplikasistoryapp.ui.OnBoardingActivity

class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_DELAY = 3000L // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        Handler().postDelayed({
            if(UserTokenPref.isLoggedIn()){
                val intent = Intent(this, HomeActivity::class.java)
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, OnBoardingActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, SPLASH_DELAY)
    }
}