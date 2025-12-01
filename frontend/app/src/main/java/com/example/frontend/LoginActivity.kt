package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.frontend.api.ApiClient
import com.example.frontend.data.models.LoginRequest
import com.example.frontend.utils.AuthPreferences
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var registerLink: TextView
    private lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authPreferences = AuthPreferences(this)

        // If already logged in, go to task list
        if (authPreferences.isLoggedIn()) {
            startActivity(Intent(this, TaskListActivity::class.java))
            finish()
            return
        }

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        registerLink = findViewById(R.id.register_link)

        loginBtn.setOnClickListener { handleLogin() }
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun handleLogin() {
        val username = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (username.isEmpty()) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            return
        }

        loginBtn.isEnabled = false

        lifecycleScope.launch {
            try {
                val authService = ApiClient.getAuthService()
                val response = authService.login(LoginRequest(username, password))

                // Save auth data
                authPreferences.saveAuth(
                    response.token,
                    response.refreshToken,
                    response.user.username,
                    response.user.id
                )

                ApiClient.setTokens(response.token, response.refreshToken)

                Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                // Navigate to task list
                startActivity(Intent(this@LoginActivity, TaskListActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                loginBtn.isEnabled = true
            }
        }
    }
}
