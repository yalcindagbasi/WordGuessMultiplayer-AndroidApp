package com.example.yazlab2proje2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yazlab2proje2.Models.User
import com.example.yazlab2proje2.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateGameWaitingActivity : AppCompatActivity() {

    private lateinit var gameId: String
    private lateinit var firebaseFirestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_game_waiting)

        gameId = intent.getStringExtra("GAME_ID").toString()
        firebaseFirestore = FirebaseFirestore.getInstance()
        val lbl_game_ID = findViewById<TextView>(R.id.lbl_game_ID)
        lbl_game_ID.text = gameId
        // Oyun oluşturma işlemi tamamlandığında bekleyen durumu kontrol et
        checkWaitingState()
    }

    private fun checkWaitingState() {
        // Belirtilen oyun kimliğine sahip bir oyun var mı kontrol et
        firebaseFirestore.collection("games")
            .document(gameId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Hata durumunda hata mesajı göster
                    Toast.makeText(this@CreateGameWaitingActivity, "Bir hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val gameState = snapshot.getString("gameState")
                    if (gameState == "JOINED") {
                        val kelime= snapshot.getString("word").toString()
                        val timelimit = snapshot.getLong("timeLimit")!!.toInt()
                        startGame(kelime, timelimit)
                        updateGameState()
                    }
                }
            }
    }

    private fun startGame(kelime : String,timelimit : Int) {
        // Oyun başlatma ekranına geç
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("GAME_ID", gameId)

intent.putExtra("TIME_LIMIT",timelimit)
intent.putExtra("WORD",kelime)
checkUserState(FirebaseAuth.getInstance().currentUser?.uid.toString())
        Log.d("GameActivity", "CREATEGAMEWAITINGACTIVITY 64 Game ID: $gameId")
        startActivity(intent)
        finish()
    }

    private fun updateGameState() {
        // Oyun durumunu "InProgress" olarak güncelle
        firebaseFirestore.collection("games")
            .document(gameId)
            .update("gameState", "INPROGRESS")
            .addOnSuccessListener {
                // Güncelleme başarılı ise bir işlem yapma
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@CreateGameWaitingActivity, "Oyun durumu güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onBackPressed() {
        // Geri butonuna basıldığında yapılacak işlemler

        // Örneğin, kullanıcının durumunu 'OFFLINE' olarak güncelleyebiliriz
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Utils.updateUserState(userId, UserState.ONLINE)
        }

        // Son olarak, super.onBackPressed() çağrısını yaparak geri butonunun varsayılan davranışını gerçekleştiririz
        super.onBackPressed()
    }
    fun checkUserState(userId: String) {
        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if (user != null) {
                    Log.d("UserState", userId+" User State: "+user.state.toString())
                }
            }
            .addOnFailureListener { e ->
                // Hata durumunda hata mesajı göster
            }
    }
}
