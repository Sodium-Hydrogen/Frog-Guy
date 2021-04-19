package com.example.frogguy

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

class LeaderboardActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        var name1 = findViewById<TextView>(R.id.name1)
        var name2 = findViewById<TextView>(R.id.name2)
        var name3 = findViewById<TextView>(R.id.name3)
        var score1 = findViewById<TextView>(R.id.score1)
        var score2 = findViewById<TextView>(R.id.score2)
        var score3 = findViewById<TextView>(R.id.score3)

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
    }


}