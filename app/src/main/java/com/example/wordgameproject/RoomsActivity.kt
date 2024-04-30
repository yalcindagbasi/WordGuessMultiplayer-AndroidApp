package com.example.wordgameproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.wordgameproject.Models.RoomType
import com.example.wordgameproject.Models.UserState
import com.google.firebase.auth.FirebaseAuth

class RoomsActivity : AppCompatActivity(){
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)

        val gameType= intent.getStringExtra("gameType")
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

        // ÇIKIŞ YAP BUTONU
        val buttonLogout = findViewById<Button>(R.id.btn_ExitGame)
        buttonLogout.setOnClickListener {
            // Uygulamadan çıkış yap
            if (userId != null) {
                Utils.updateUserState(userId, UserState.OFFLINE)
            }
            finishAffinity()

        }
        val btn_room4 = findViewById<Button>(R.id.btn_length4)
        btn_room4.setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            if(gameType == "1"){
                intent.putExtra("roomType", RoomType.type1length4)

            }else{
                intent.putExtra("roomType", RoomType.type2length4)

            }

            intent.putExtra("gameType", gameType)
            intent.putExtra("lettercount",4)
            startActivity(intent)
            finish()
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
            intent.putExtra("lettercount",5)
            startActivity(intent)
            finish()
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
            intent.putExtra("lettercount",6)
            startActivity(intent)
            finish()
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
            intent.putExtra("lettercount",7)
            startActivity(intent)
            finish()
        }



    }

}
