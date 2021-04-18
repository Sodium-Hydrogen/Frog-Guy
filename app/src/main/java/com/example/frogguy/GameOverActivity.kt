package com.example.frogguy

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GameOverActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var currentScore = ""
    var found = false

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)


        var currentUserId = auth.currentUser.uid
        var gameResultText = findViewById<TextView>(R.id.gameResultText)
        var scoreText = findViewById<TextView>(R.id.scoreText)
        var scoreValue = findViewById<TextView>(R.id.scoreValue)
        var leaderboardBtn = findViewById<Button>(R.id.leaderboardButton)
        var mainBtn = findViewById<Button>(R.id.mainButton)
        var logoutBtn = findViewById<Button>(R.id.logoutButton)



        //we need the score from somewhere. Right now I'm just setting it to 100
        var newScore = 300

        scoreValue.text = newScore.toString()

        //save the score to disk
        var sharedPreference : SharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreference.edit()

        var db = FirebaseFirestore.getInstance()


        db.collection("Scores").document(currentUserId).get()
            .addOnCompleteListener{
                if(it.isSuccessful) {
                    var strScore = it.result?.data?.get("score")?.toString()
                    if (strScore != null) {
                        currentScore = strScore
                        Toast.makeText(this, "Score already exists", Toast.LENGTH_SHORT).show()
                        if(newScore > currentScore.toInt()) {
                            db.collection("Scores").document(currentUserId).update("score", newScore.toString())
                            currentScore = newScore.toString()
//                            val score : String? = sharedPreference.getString("score", null)
//                            if(score == null) {
//                                editor.apply {
//                                    putString("score", currentScore)
//                                }.apply()
//                            }
//                            else {
//                                sharedPreference.edit().remove("score").commit()
//                                editor.apply {
//                                    putString("score", currentScore)
//                                }.apply()
//                            }
                        }
//                        else {
//                            editor.apply {
//                                putString("score", currentScore)
//                            }.apply()
//                        }
                    }
                    else {
                        Toast.makeText(this, "document does not exist", Toast.LENGTH_SHORT).show()
                        var score : MutableMap<String, Any?> = HashMap()
                        score["userId"] = auth.currentUser.uid
                        score["score"] = newScore
                        db.collection("Scores").document(currentUserId).set(score)
                            .addOnCompleteListener {doc ->
                                if(doc.isSuccessful) {
                                    Toast.makeText(this, "Your score has been added.", Toast.LENGTH_SHORT).show()
                                    currentScore = newScore.toString()
//                                    editor.apply {
//                                        putString("score", newScore.toString())
//                                    }.apply()
                                }
                                else {
                                    Toast.makeText(this, "Could not add score to db", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }


        leaderboardBtn.setOnClickListener {
            var intent = Intent(this, LeaderboardActivity::class.java)
            intent.putExtra("score", currentScore)
            startActivity(intent)
        }

        mainBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            var alert = AlertDialog.Builder(this)
            alert.setTitle("Logging out")
            alert.setMessage("Are you sure you want to log out?")
            alert.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
            }
            alert.setNegativeButton("No") { _: DialogInterface, _: Int -> }
            alert.show()
        }
    }


}
