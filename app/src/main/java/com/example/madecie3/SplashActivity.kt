package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme before any UI is rendered
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Track launch count
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val launchCount = prefs.getInt("app_launch_count", 0) + 1
        prefs.edit().putInt("app_launch_count", launchCount).apply()

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            val nextScreen = if (user != null) {
                if (user.email == "deliverypartner@gmail.com") {
                    DeliveryDashboardActivity::class.java
                } else {
                    DashboardActivity::class.java
                }
            } else {
                LoginActivity::class.java
            }

            prefs.edit().putString("last_destination", nextScreen.simpleName).apply()

            startActivity(Intent(this, nextScreen))
            finish()
        }, 2000)
    }
}
