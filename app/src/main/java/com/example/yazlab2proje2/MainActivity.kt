package com.example.yazlab2proje2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var database: DatabaseReference
    private lateinit var roomId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val et_gameID= findViewById<EditText>(R.id.txtfield_EnterGameID)

        // Kullanıcı adını alarak hoş geldiniz mesajını güncelle
        val textViewWelcome = findViewById<TextView>(R.id.textViewWelcome)
        val username = intent.getStringExtra("USERNAME")
        textViewWelcome.text = "Hoş Geldiniz, $username!"

        //OYUN OLUŞTUR BUTONU
        val btncreateGame= findViewById<Button>(R.id.btn_CreateGameActivity)
        btncreateGame.setOnClickListener{
            createGameRoom()

        }

        //OYUNA KATIL BUTONU
        val btnjoingame= findViewById<Button>(R.id.btn_JoinGame)

        btnjoingame.setOnClickListener{
            roomId=et_gameID.text.toString()
            joinGameRoom()

        }


        //ANASAYFA BUTONU
        val btnHomePage = findViewById<Button>(R.id.btn_HomePage)
        btnHomePage.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //HESAPTAN ÇIKIŞ YAP BUTONU
        val btnSignOut = findViewById<Button>(R.id.btn_Logout)
        btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        //ÇIKIŞ YAP BUTONU
        val buttonLogout = findViewById<Button>(R.id.btn_ExitGame)
        buttonLogout.setOnClickListener {
            finish()
        }
    }
    private fun createGameRoom() {

        val intent = Intent(this, CreateGameActivity::class.java)

        startActivity(intent)
    }

    private fun joinGameRoom() {
        val intent = Intent(this, JoinGameActivity::class.java)
        intent.putExtra("GAME_ID",roomId)
        startActivity(intent)
    }

}
