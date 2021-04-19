package com.example.frogguy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        var scoresList = findViewById<ListView>(R.id.scoresList)
        var allScores = ArrayList<String>()
        var allScorePairs = ArrayList<Pair<String, Int>>()
        var mainBtn = findViewById<Button>(R.id.mainButton)
        var map :MutableMap<String, Int> = HashMap()
        var sortedMap : MutableMap<String, Int> = HashMap()

        var db = FirebaseFirestore.getInstance()



        mainBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        db.collection("Scores").get()
                .addOnCompleteListener { scores ->
                    Log.i("score", "here")
                    if(scores.isSuccessful) {
                        for(score in scores.result!!) {
                            var userScore = score.data["score"].toString()
                            var userEmail = score.data["email"].toString()
                            var scorePair = Pair(userEmail, userScore.toInt())
                            allScorePairs.add(scorePair)

                            allScorePairs.sortedBy { (key, value) -> value}
                        }
                        for(scorePair in allScorePairs) {
                            var fullScore = "${scorePair.first} - ${scorePair.second}"
                            Log.i("score", allScores.toString())
                            allScores.add(fullScore)
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