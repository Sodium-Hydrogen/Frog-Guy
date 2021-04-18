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
        var newScore = 100

        scoreValue.text = newScore.toString()

        //save the score to disk
        var sharedPreference : SharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreference.edit()

        var db = FirebaseFirestore.getInstance()


        db.collection("Scores").document(currentUserId).get()
            .addOnCompleteListener{
                Log.i("here", "${it.isSuccessful}")
                if(it.isSuccessful) {
                    var strScore = it.result?.data?.get("score")?.toString()
                    if (strScore != null) {
                        currentScore = strScore
                        Toast.makeText(this, "Score already exists", Toast.LENGTH_SHORT).show()
                        //comparing times
                        if(newScore > currentScore.toInt()) {
                            db.collection("Scores").document(currentUserId).update("score", newScore.toString())
                        }
                    }
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
                            }
                            else {
                                Toast.makeText(this, "Could not add score to db", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }


//        if(currentScore == "") {
//            Toast.makeText(this, currentScore, Toast.LENGTH_SHORT).show()
//            var score : MutableMap<String, Any?> = HashMap()
//            score["userId"] = auth.currentUser.uid
//            score["score"] = newScore
//            db.collection("Scores").document(currentUserId).set(score)
//                .addOnCompleteListener {
//                    if(it.isSuccessful) {
//                        Toast.makeText(this, "Your score has been added.", Toast.LENGTH_SHORT).show()
//                    }
//                    else {
//                        Toast.makeText(this, "Could not add score to db", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//        else {
//            Toast.makeText(this, "Score already exists", Toast.LENGTH_SHORT).show()
//            //comparing times
//            if(newScore > currentScore.toInt()) {
//                db.collection("Scores").document(currentUserId).update("score", newScore.toString())
//            }
//        }


        leaderboardBtn.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
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

    fun findScore(db: FirebaseFirestore): Unit {
        var currentUserId = auth.currentUser.uid
        db.collection("Scores").document(currentUserId).get()
            .addOnSuccessListener {
                Toast.makeText(this, it.id, Toast.LENGTH_SHORT).show()
                if(it.exists()) {
                    Toast.makeText(this, "document exists", Toast.LENGTH_SHORT).show()
                    var strScore = it["score"]!!.toString()
                    currentScore = strScore
                }
                else {
                    Toast.makeText(this, "document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
    }


}
