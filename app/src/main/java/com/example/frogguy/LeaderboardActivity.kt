package com.example.frogguy

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {
    var currentScore : String = "None"
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        var name0 = findViewById<TextView>(R.id.name0)
        var name1 = findViewById<TextView>(R.id.name1)
        var name2 = findViewById<TextView>(R.id.name2)
        var score0 = findViewById<TextView>(R.id.score0)
        var score1 = findViewById<TextView>(R.id.score1)
        var score2 = findViewById<TextView>(R.id.score2)

        var db = FirebaseFirestore.getInstance()

        //to get score from last player
        var score = intent.getStringExtra("score")
//        val sharedPreference : SharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
//        val editor : SharedPreferences.Editor = sharedPreference.edit()
//        val score : String? = sharedPreference.getString("score", null)


        var highScores = arrayListOf(score0.text.toString(), score1.text.toString(), score2.text.toString())


        if(score != null) {
            db.collection("Leaderboard").document(0.toString()).get()
                .addOnCompleteListener {

                }

        }

//        if(score != null) {
//            Log.i("score", score)
//            //check if the leaderboard has been initialized
//            db.collection("Leaderboard").document(0.toString()).get()
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        var strScore = it.result?.data?.get("score")?.toString()
//                        if (strScore == null) {
//                            initializeHighScores(db)
//                            updateHighScore(db, 0, score)
//                            return@addOnCompleteListener
//                        }
//                        else {
//                            //update high scores array with score values
//                            for(i in 0..2) {
//                                db.collection("Leaderboard").document(i.toString()).get()
//                                        .addOnCompleteListener {
//                                            if (it.isSuccessful) {
//                                                var strScore = it.result?.data?.get("score")?.toString()
//                                                Log.i("score", "strScore $strScore")
//                                                if (strScore != null) {
//                                                    Log.i("score", "here")
//                                                    highScores[i] = strScore
//                                                    Log.i("score", highScores.toString())
//                                                }
//                                            }
//                                        }
//                            }
//                            Log.i("score", highScores.toString())
//                            if(hasScore(highScores, score)) {
//                                Log.i("score", "In contains score")
//                                return@addOnCompleteListener
//                            }
//                            else {
//                                for ((index, highScore) in highScores.withIndex()) {
//                                    if(highScore != "None" && score.toInt() > highScores[index].toInt()) {
//                                        var temp = highScore
//                                        highScores[index] = score
//                                        updateHighScore(db, index, score)
//                                        if(index != 2) {
//                                            if(highScores[index + 1] == "None") {
//                                                highScores[index + 1] = temp
//                                                updateHighScore(db, index + 1, temp)
//                                                return@addOnCompleteListener
//                                            }
//                                            else {
//                                                var temp2 = highScores[index + 1]
//                                                highScores[index + 1] = temp
//                                                updateHighScore(db, index + 1, temp)
//                                                if(index + 1 != 2) {
//                                                    highScores[index + 2] = temp2
//                                                    updateHighScore(db, index + 2, temp2)
//                                                    return@addOnCompleteListener
//                                                }
//                                                return@addOnCompleteListener
//                                            }
//                                        }
//                                        else {
//                                            return@addOnCompleteListener
//                                        }
//                                    }
//                                    else if(highScore == "None") {
//                                        highScores[index] = score
//                                        updateHighScore(db, index, score)
//                                        return@addOnCompleteListener
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//        }

        db.collection("Leaderboard").document(0.toString()).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        name0.text = it.result?.data?.get("email").toString()
                        score0.text = it.result?.data?.get("score").toString()
                    }
                }
        db.collection("Leaderboard").document(1.toString()).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        name1.text = it.result?.data?.get("email").toString()
                        score1.text = it.result?.data?.get("score").toString()
                    }
                }

        db.collection("Leaderboard").document(2.toString()).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        name2.text = it.result?.data?.get("email").toString()
                        score2.text = it.result?.data?.get("score").toString()
                    }
                }
    }

    private fun updateHighScoresArr(db: FirebaseFirestore, highScores: ArrayList<String>): ArrayList<String> {
        for(i in 0..2) {
            db.collection("Leaderboard").document(i.toString()).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            var strScore = it.result?.data?.get("score")?.toString()
                            Log.i("score", "strScore $strScore")
                            if (strScore != null) {
                                Log.i("score", "here")
                                highScores[i] = strScore
                            }
                        }
                    }
        }
        return highScores
    }

    private fun initializeHighScores(db: FirebaseFirestore): Unit {
        for(i in 0..2) {
            var newHighScore : MutableMap<String, Any?> = HashMap()
            newHighScore["userId"] = "None"  //this will get changed later so doesn't matter
            newHighScore["score"] = "None"  //this will get changed later so doesn't matter
            newHighScore["email"] = "None"
            db.collection("Leaderboard").document(i.toString()).set(newHighScore)
        }

    }

    private fun updateHighScore(db: FirebaseFirestore, index: Int, score: String): Unit {
        db.collection("Leaderboard").document(index.toString()).update(mapOf("userId" to auth.currentUser.uid, "email" to auth.currentUser.email,"score" to score))
    }

    private fun hasAllNone(highScores: ArrayList<String>): Boolean {
        for(highScore in highScores) {
            if(highScore != "None") {
                return false
            }
        }
        return true
    }

    private fun hasScore(highScores: ArrayList<String>, score: String): Boolean {
        for(highScore in highScores) {
            Log.i("score", "${highScore} == ${score}: ${highScore == score}")
            if(highScore == score) {
                return true
            }
        }
        return false
    }

}