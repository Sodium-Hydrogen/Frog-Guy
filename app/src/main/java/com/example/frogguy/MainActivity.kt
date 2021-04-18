package com.example.frogguy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var leaderboardBtn = findViewById<Button>(R.id.showLeaderboardButton)
        var newSignUpBtn = findViewById<Button>(R.id.newSignUpButton)
        var loginBtn = findViewById<Button>(R.id.loginButton)
        var b = findViewById<Button>(R.id.bplay)
        var gameOverBtn = findViewById<Button>(R.id.gameOverButton) // this button is just to test how things are working

//        var currentUser = auth.currentUser
//
//        Toast.makeText(this, "${currentUser}", Toast.LENGTH_SHORT).show()

        b.setOnClickListener {
            startActivity(Intent(this,GameActivity::class.java))
        }
        newSignUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        leaderboardBtn.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        gameOverBtn.setOnClickListener {
            startActivity(Intent(this, GameOverActivity::class.java))
        }
    }
}