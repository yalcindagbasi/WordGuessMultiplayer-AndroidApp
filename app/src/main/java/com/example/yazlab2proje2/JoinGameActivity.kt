package com.example.yazlab2proje2

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class JoinGameActivity : AppCompatActivity() {

    private lateinit var gameId: String
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingame)

        gameId = intent.getStringExtra("GAME_ID").toString()
        firebaseFirestore = FirebaseFirestore.getInstance()
        val lbl_gameID = findViewById<TextView>(R.id.lbl_GameID)
        lbl_gameID.text= gameId
        // Odaya katılma işlemi tamamlandığında bekleyen durumu kontrol et
        checkWaitingState()
    }

    private fun checkWaitingState() {
        // Belirtilen oyun kimliğine sahip bir oyun var mı kontrol et
        firebaseFirestore.collection("games")
            .document(gameId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Hata durumunda hata mesajı göster
                    Toast.makeText(this@JoinGameActivity, "Bir hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val gameState = snapshot.getString("gameState")
                    if (gameState == "JOINED") {
                        // Oyun başladığında oyuna geç

                        val kelime = snapshot.getString("word")
                        val timelimit = snapshot.getLong("timeLimit")!!.toInt()
                        if (kelime != null) {
                            startGame(kelime,timelimit)
                        }
                        else {
                            Toast.makeText(this@JoinGameActivity, "Kelime alınamadı", Toast.LENGTH_SHORT).show()
                            checkWaitingState()

                        }
                    }
                }
            }
    }

    private fun startGame(kelime : String, timelimit : Int  ) {
        // Oyun başlatma ekranına geç
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("GAME_ID", gameId)
        intent.putExtra("WORD", kelime)
        intent.putExtra("TIME_LIMIT", timelimit)

        startActivity(intent)
        finish()
    }
}
