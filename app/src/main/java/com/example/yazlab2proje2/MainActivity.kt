package com.example.yazlab2proje2
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.yazlab2proje2.Models.GameModel
import com.example.yazlab2proje2.Models.GameStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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
        val textViewWelcome = findViewById<TextView>(R.id.textViewWelcome)
        val username = intent.getStringExtra("USERNAME")
        textViewWelcome.text = "Hoş Geldiniz, $username!"

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
            finish()
        }

        // ÇIKIŞ YAP BUTONU
        val buttonLogout = findViewById<Button>(R.id.btn_ExitGame)
        buttonLogout.setOnClickListener {
            // Uygulamadan çıkış yap
            finish()
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

        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model= it?.toObject(GameModel::class.java)
                if(model!=null){
                    model.gameState=GameStatus.JOINED
                    model.player2Id = firebaseAuth.currentUser?.uid ?: ""
                    GameData.saveGameModel(model)
                    val intent = Intent(this@MainActivity, JoinGameActivity::class.java)
                    intent.putExtra("GAME_ID", gameId)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this@MainActivity, "Belirtilen kimliğe sahip bir oyun bulunamadı", Toast.LENGTH_SHORT).show()

                }
            }
    }
}
