package com.example.yazlab2proje2
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.yazlab2proje2.Models.GameModel
import com.example.yazlab2proje2.Models.GameStatus
import com.example.yazlab2proje2.Models.User
import com.example.yazlab2proje2.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Kullanıcı adını alarak hoş geldiniz mesajını güncelle
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        if (userId != null) {
            Utils.updateUserState(userId, UserState.ONLINE)
        }
        db.collection("users")
            .document(userId!!)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                val textViewWelcome = findViewById<TextView>(R.id.textViewWelcome)
                Log.d("TAG", "username: ${user?.username}")
                Log.d("TAG", "userid: ${userId}")
                textViewWelcome.text = "Hoş Geldiniz, ${user?.username}!"

            }

        // OYUN OLUŞTUR BUTONU
        val btncreateGame = findViewById<Button>(R.id.btn_CreateGameActivity)
        btncreateGame.setOnClickListener{
            createGameRoom()
        }

        // OYUNA KATIL BUTONU
        val btnjoingame = findViewById<Button>(R.id.btn_JoinGame)
        btnjoingame.setOnClickListener{
            joinGameRoom()
        }
        val btnGameTypeRoom = findViewById<Button>(R.id.btn_JoinRooms)
        btnGameTypeRoom.setOnClickListener {
            val intent = Intent(this, GameTypeActivity::class.java)
            startActivity(intent)
        }
        // ANASAYFA BUTONU
        val btnHomePage = findViewById<Button>(R.id.btn_HomePage)
        btnHomePage.setOnClickListener {
            // Ana sayfaya geri dön
            finish()
        }


        // HESAPTAN ÇIKIŞ YAP BUTONU
        val btnSignOut = findViewById<Button>(R.id.btn_Logout)
        btnSignOut.setOnClickListener {
            // Firebase oturumu kapat ve giriş ekranına yönlendir
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            Utils.updateUserState(userId, UserState.OFFLINE)
            finish()
        }

        // ÇIKIŞ YAP BUTONU
        val buttonLogout = findViewById<Button>(R.id.btn_ExitGame)
        buttonLogout.setOnClickListener {
            // Uygulamadan çıkış yap
            Utils.updateUserState(userId, UserState.OFFLINE)
            finishAffinity()

        }
    }

    private fun createGameRoom() {
        val intent = Intent(this, CreateGameActivity::class.java)
        startActivity(intent)
    }

    private fun joinGameRoom() {
        val etGameID = findViewById<EditText>(R.id.txtfield_EnterGameID)
        val gameId = etGameID.text.toString()

        // Girilen oyun kimliğini kontrol et
        if (gameId.isEmpty()) {
            Toast.makeText(this, "Lütfen bir oyun kimliği girin", Toast.LENGTH_SHORT).show()
            return
        }
        intent = Intent(this, JoinGameActivity::class.java)
        intent.putExtra("GAME_ID", gameId)
        intent.putExtra("player2Id", FirebaseAuth.getInstance().currentUser?.uid)
        startActivity(intent)

    }
    /*override fun onStop() {
        super.onStop()

        // Kullanıcının UserState'ini OFFLINE olarak güncelle
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Utils.updateUserState(userId, UserState.OFFLINE)
        }
    }*/
}
