package com.example.frogguy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.absoluteValue
import kotlin.properties.Delegates

class LeaderboardActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        var scoresList = findViewById<ListView>(R.id.scoresList)
        var allScores = ArrayList<String>()
        var allScorePairs = ArrayList<Pair<String, Int>>()
        var backButton = findViewById<Button>(R.id.backButton)
        var map :MutableMap<String, Int> = HashMap()
        var sortedMap : MutableMap<String, Int> = HashMap()

        var db = FirebaseFirestore.getInstance()

        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        db.collection("Scores").get()
                .addOnCompleteListener { scores ->
                    if(scores.isSuccessful) {
                        for(score in scores.result!!) {
                            var userScore = score.data["score"].toString()
                            var userEmail = score.data["email"].toString()
                            var scorePair = Pair(userEmail, userScore.toInt())
                            Log.i("score", "Score pair: ${scorePair}")
                            allScorePairs.add(scorePair)

                            allScorePairs.sortedBy { (key, value) -> value}

//                            map.put(userEmail, userScore.toInt())
////                            sortedMap = map.toSortedMap()
//
//                            map.toList()
//                                    .sortedBy { (key, value) -> value }
//                                    .toMap()
//                            Log.i("score", sortedMap.toString())
                            Log.i("score", allScorePairs.toString())
                        }
                        for(scorePair in allScorePairs) {
                            var fullScore = "${scorePair.first} - ${scorePair.second}"
                            Log.i("score", "Full score: $fullScore")
                            allScores.add(fullScore)
                            Log.i("score", "all scores array: ${allScores.toString()}")
                        }
                        var listAdapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, allScores)
                        scoresList.adapter = myAdapter(allScores, this)
                    }
                }
    }




    class myAdapter(var data: ArrayList<String>, var context: Context): BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val tv = TextView(context)
            tv.text = data[position]
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25F)
            tv.setPadding(10, 10, 10, 10)
            return tv
        }

        override fun getItem(position: Int): Any {
            return 0
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

    }


}