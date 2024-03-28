package com.example.yazlab2proje2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        // Kullanıcı adını alarak hoş geldiniz mesajını güncelle
        val textViewWelcome = findViewById<TextView>(R.id.textViewWelcome)
        val username = intent.getStringExtra("USERNAME")
        textViewWelcome.text = "Hoş Geldiniz, $username!"

        //OYUN OLUŞTUR BUTONU
        val btncreateGame= findViewById<Button>(R.id.btn_CreateGameActivity)
        btncreateGame.setOnClickListener{
            val intent = Intent(this, CreateGameActivity::class.java)
            startActivity(intent)
        }

        //OYUNA KATIL BUTONU
        val btnjoingame= findViewById<Button>(R.id.btn_JoinGame)

        btnjoingame.setOnClickListener{
            val intent = Intent(this, FindGameActivity::class.java)
            startActivity(intent)
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
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //ÇIKIŞ YAP BUTONU
        val buttonLogout = findViewById<Button>(R.id.btn_ExitGame)
        buttonLogout.setOnClickListener {
            finish()
        }
    }

}
