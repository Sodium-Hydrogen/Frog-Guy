package com.example.frogguy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        var db = FirebaseFirestore.getInstance()
        var logoutBtn = findViewById<Button>(R.id.logoutButton)

        if(auth.currentUser != null) {
            loginBtn.isVisible = false
            newSignUpBtn.isVisible = false
            logoutBtn.isVisible = true
        }

        var newScore = intent.getIntExtra("score", -1)
        if (newScore != -1) {
            var newScoreUserEmail = auth.currentUser.email
            var newScoreUserId = auth.currentUser.uid

            db.collection("Scores").document(newScoreUserId).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            var strScore = it.result?.data?.get("score")?.toString()
                            if (strScore != null) {
                                if (newScore > strScore.toInt()) {
                                    Toast.makeText(this, "Your score has been updated!", Toast.LENGTH_SHORT).show()
                                    db.collection("Scores").document(newScoreUserId).update("score", newScore.toString())
                                }
                            } else {
                                var score: MutableMap<String, Any?> = HashMap()
                                score["email"] = newScoreUserEmail
                                score["score"] = newScore
                                db.collection("Scores").document(newScoreUserId).set(score)
                                        .addOnCompleteListener { doc ->
                                            if (doc.isSuccessful) {
                                                Toast.makeText(this, "Your score has been added!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(this, "Could not add score to the database.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                            }
                        }
                    }


        }

        b.setOnClickListener {
            if(auth.currentUser != null) {
                startActivity(Intent(this, GameActivity::class.java))
            }
            else {
                Toast.makeText(this, "You must be signed in to play.", Toast.LENGTH_SHORT).show()
            }
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
        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}