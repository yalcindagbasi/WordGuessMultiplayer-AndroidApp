package com.example.yazlab2proje2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class GameTypeActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gametypes)

        val btn_gameType1 = findViewById<Button>(R.id.btn_gameType1)
        btn_gameType1.setOnClickListener {
            val intent = Intent(this, RoomsActivity::class.java)
            intent.putExtra("gameType", "1")

            startActivity(intent)
        }
        val btn_gameType2 = findViewById<Button>(R.id.btn_gameType2)
        btn_gameType2.setOnClickListener {
            val intent = Intent(this, RoomsActivity::class.java)
            intent.putExtra("gameType", "2")
            startActivity(intent)
        }


    }
}