package com.example.yazlab2proje2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.yazlab2proje2.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GameTypeActivity : AppCompatActivity(){
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gametypes)
        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            Utils.updateUserState(userId, UserState.ONLINE)
        }
// ANASAYFA BUTONU
        val btnHomePage = findViewById<Button>(R.id.btn_HomePage)
        btnHomePage.setOnClickListener {
            // Ana sayfaya geri dön
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        // HESAPTAN ÇIKIŞ YAP BUTONU
        val btnSignOut = findViewById<Button>(R.id.btn_Logout)
        btnSignOut.setOnClickListener {
            // Firebase oturumu kapat ve giriş ekranına yönlendir
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            if (userId != null) {
                Utils.updateUserState(userId, UserState.OFFLINE)
            }
            startActivity(intent)
            finish()
        }

        val buttonLogout = findViewById<Button>(R.id.btn_ExitGame)
        buttonLogout.setOnClickListener {
            // Uygulamadan çıkış yap
            if (userId != null) {
                Utils.updateUserState(userId, UserState.OFFLINE)
            }
            finishAffinity()

        }
        val btn_gameType1 = findViewById<Button>(R.id.btn_gameType1)
        btn_gameType1.setOnClickListener {
            val intent = Intent(this, RoomsActivity::class.java)
            intent.putExtra("gameType", "1")

            startActivity(intent)
            finish()
        }
        val btn_gameType2 = findViewById<Button>(R.id.btn_gameType2)
        btn_gameType2.setOnClickListener {
            val intent = Intent(this, RoomsActivity::class.java)
            intent.putExtra("gameType", "2")
            startActivity(intent)
            finish()
        }


    }
}