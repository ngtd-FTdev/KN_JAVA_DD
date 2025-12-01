package com.example.frontend_nhom1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.frontend_nhom1.model.LoginRequest
import com.example.frontend_nhom1.network.ApiClient
import com.example.frontend_nhom1.util.PreferencesHelper
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var edtUser: EditText
    private lateinit var edtPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        PreferencesHelper.init(this)

        edtUser = findViewById(R.id.editUsername)
        edtPass = findViewById(R.id.editPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progress = findViewById(R.id.loginProgress)

        btnLogin.setOnClickListener {
            val u = edtUser.text.toString().trim()
            val p = edtPass.text.toString()
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            doLogin(u, p)
        }
    }

    private fun doLogin(username: String, password: String) {
        progress.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        lifecycleScope.launch {
            try {
                val service = ApiClient.getService(this@LoginActivity)
                val resp = service.login(LoginRequest(username, password))
                if (resp.isSuccessful) {
                    val body = resp.body()
                    val token = body?.token
                    if (!token.isNullOrEmpty()) {
                        PreferencesHelper.get(this@LoginActivity).saveToken(token)
                        startActivity(Intent(this@LoginActivity, TaskListActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${resp.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: Exception) {
                Toast.makeText(this@LoginActivity, "Network error: ${ex.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progress.visibility = View.GONE
                btnLogin.isEnabled = true
            }
        }
    }
}
