package com.example.yazlab2proje2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.yazlab2proje2.Models.RoomType

class RoomsActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)

        val gameType= intent.getStringExtra("gameType")


        val btn_room4 = findViewById<Button>(R.id.btn_length4)
        btn_room4.setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            if(gameType == "1"){
                intent.putExtra("roomType", RoomType.type1length4)
            }else{
                intent.putExtra("roomType", RoomType.type2length4)
            }

            intent.putExtra("gameType", gameType)
            intent.putExtra("wordLength", 4)
            startActivity(intent)
        }
        val btn_room5 = findViewById<Button>(R.id.btn_length5)
        btn_room5.setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            if(gameType == "1"){
                intent.putExtra("roomType", RoomType.type1length5)
            }else{
                intent.putExtra("roomType", RoomType.type2length5)
            }
            intent.putExtra("gameType", gameType)
            intent.putExtra("wordLength", 5)
            startActivity(intent)
        }
        val btn_room6 = findViewById<Button>(R.id.btn_length6)
        btn_room6.setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            if(gameType == "1"){
                intent.putExtra("roomType", RoomType.type1length6)
            }else{
                intent.putExtra("roomType", RoomType.type2length6)
            }
            intent.putExtra("gameType", gameType)
            intent.putExtra("wordLength", 6)
            startActivity(intent)
        }
        val btn_room7 = findViewById<Button>(R.id.btn_length7)
        btn_room7.setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            if (gameType == "1") {
                intent.putExtra("roomType", RoomType.type1length7)
            } else {
                intent.putExtra("roomType", RoomType.type2length7)
            }
            intent.putExtra("gameType", gameType)
            intent.putExtra("wordLength", 7)
            startActivity(intent)
        }



    }

}
