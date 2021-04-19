package com.example.frogguy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
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
        var gameOverBtn = findViewById<Button>(R.id.gameOverButton) // this button is just to test how things are working
        var db = FirebaseFirestore.getInstance()

        var newScore = intent.getIntExtra("score", -1)
        if (newScore != -1) {
            var newScoreUserEmail = auth.currentUser.email
            var newScoreUserId = auth.currentUser.uid

            db.collection("Scores").document(newScoreUserId).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            var strScore = it.result?.data?.get("score")?.toString()
                            if (strScore != null) {
                                Toast.makeText(this, "Score already exists", Toast.LENGTH_SHORT).show()
                                if (newScore > strScore.toInt()) {
                                    Log.i("score", "score is larger than current score in db")
                                    Toast.makeText(this, "Updated Score", Toast.LENGTH_SHORT).show()
                                    db.collection("Scores").document(newScoreUserId).update("score", newScore.toString())
                                }
                            } else {
                                Toast.makeText(this, "document does not exist", Toast.LENGTH_SHORT).show()
                                var score: MutableMap<String, Any?> = HashMap()
                                score["email"] = newScoreUserEmail
                                score["score"] = newScore
                                db.collection("Scores").document(newScoreUserId).set(score)
                                        .addOnCompleteListener { doc ->
                                            if (doc.isSuccessful) {
                                                Toast.makeText(this, "Your score has been added.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(this, "Could not add score to db", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                            }
                        }
                    }


        }

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