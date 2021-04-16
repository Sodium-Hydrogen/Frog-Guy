package com.example.frogguy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var leaderboardBtn = findViewById<Button>(R.id.showLeaderboardButton)
        var newSignUpBtn = findViewById<Button>(R.id.newSignUpButton)
        var loginButton = findViewById<Button>(R.id.loginButton)
        var b = findViewById<Button>(R.id.bplay)

        b.setOnClickListener {
            startActivity(Intent(this,GameActivity::class.java))
        }
        newSignUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}