package com.example.yazlab2proje2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class CreateGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creategame)

        val btnCreateGame = findViewById<Button>(R.id.btn_CreateGame)
        btnCreateGame.setOnClickListener{
            val intent = Intent(this,GameActivity::class.java )
            startActivity(intent)
            finish()
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
}
