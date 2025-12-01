package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.api.ApiClient
import com.example.frontend.utils.AuthPreferences

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val authPreferences = AuthPreferences(this)

        // Delay 1.5 seconds before navigating
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (authPreferences.isLoggedIn()) {
                // Set tokens in API client
                authPreferences.getToken()?.let { token ->
                    authPreferences.getRefreshToken()?.let { refreshToken ->
                        ApiClient.setTokens(token, refreshToken)
                    }
                }
                Intent(this, TaskListActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        }, 1500)
    }
}
