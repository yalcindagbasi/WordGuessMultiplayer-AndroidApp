package com.example.yazlab2proje2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yazlab2proje2.Models.GameModel
import com.example.yazlab2proje2.Models.GameStatus
import com.example.yazlab2proje2.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class JoinGameActivity : AppCompatActivity() {

    private lateinit var gameId: String
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var gameListener: ListenerRegistration? = null // Listener'ı tutacak değişkeni tanımlayın


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingame)

        gameId = intent.getStringExtra("GAME_ID").toString()
        firebaseFirestore = FirebaseFirestore.getInstance()
        val player2Id= intent.getStringExtra("player2Id").toString()
        val lbl_gameID = findViewById<TextView>(R.id.lbl_GameID)
        lbl_gameID.text= gameId
        // Odaya katılma işlemi tamamlandığında bekleyen durumu kontrol et

        Firebase.firestore.collection("games")
            .document(gameId)
            .get()
            .addOnSuccessListener {
                val model= it?.toObject(GameModel::class.java)
                if(model!=null){
                    model.gameState= GameStatus.JOINED
                    model.player2Id = player2Id
                    GameData.saveGameModel(model)


                    checkWaitingState()
                }
                else{
                    Toast.makeText(this@JoinGameActivity, "Belirtilen kimliğe sahip bir oyun bulunamadı", Toast.LENGTH_SHORT).show()
                    finish()

                }
            }

    }

    private fun checkWaitingState() {
        gameListener?.remove()
        // Belirtilen oyun kimliğine sahip bir oyun var mı kontrol et
        gameListener = firebaseFirestore.collection("games")
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
                        val gameModel = snapshot.toObject(GameModel::class.java)
                if (gameModel!=null) {
                    GameData.saveGameModel(gameModel)

                    val kelime = snapshot.getString("word")
                    val timelimit = snapshot.getLong("timeLimit")!!.toInt()
                    if (kelime != null) {
                        startGame(kelime, timelimit)
                    } else {
                        Toast.makeText(
                            this@JoinGameActivity,
                            "Kelime alınamadı",
                            Toast.LENGTH_SHORT
                        ).show()
                        checkWaitingState()

                    }
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
        Log.d("JoinGameActivity", "JoinGameActivity 94 Game ID: $gameId")
        startActivity(intent)
        finish()
    }
    override fun onBackPressed() {
        // Geri butonuna basıldığında yapılacak işlemler
        gameListener?.remove()

        // Örneğin, kullanıcının durumunu 'OFFLINE' olarak güncelleyebiliriz
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Utils.updateUserState(userId, UserState.ONLINE)
        }

        // Son olarak, super.onBackPressed() çağrısını yaparak geri butonunun varsayılan davranışını gerçekleştiririz
        super.onBackPressed()
    }
}
