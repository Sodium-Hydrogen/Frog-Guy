package com.example.frogguy

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.w3c.dom.Text

class LeaderboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        var name1 = findViewById<TextView>(R.id.name1)
        var name2 = findViewById<TextView>(R.id.name2)
        var name3 = findViewById<TextView>(R.id.name3)
        var score1 = findViewById<TextView>(R.id.score1)
        var score2 = findViewById<TextView>(R.id.score2)
        var score3 = findViewById<TextView>(R.id.score3)


        var highScores = arrayListOf(score1.text.toString(), score2.text.toString(), score3.text.toString())

    }
}