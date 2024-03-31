package com.example.yazlab2proje2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class JoinGameActivity : AppCompatActivity() {
    var game_ID : String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingame)
        val lbl_gameID=findViewById<TextView>(R.id.lbl_GameID)
        game_ID= intent.getStringExtra("GAME_ID").toString()
        lbl_gameID.setText(game_ID)
        val btn_cancel=findViewById<Button>(R.id.btn_cancel)
        btn_cancel.setOnClickListener {
            finish()
        }
    }
}
