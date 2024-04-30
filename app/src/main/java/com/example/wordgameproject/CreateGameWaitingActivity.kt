package com.example.wordgameproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordgameproject.Models.RoomType
import com.example.wordgameproject.Models.User
import com.example.wordgameproject.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CreateGameWaitingActivity : AppCompatActivity() {

    private lateinit var gameId: String
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var isGameStarted = false
    private var gameStartListener : ListenerRegistration? = null
    private lateinit var curGametype : RoomType

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
        gameStartListener =firebaseFirestore.collection("games")
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


                        gameStartListener?.remove()
                        if(snapshot.get("gameType").toString() == RoomType.type0.toString()){
                            val kelime= snapshot.getString("player1word").toString()
                            val timelimit = snapshot.getLong("timeLimit")!!.toInt()
                            startGame(kelime, timelimit)
                            updateGameState()
                        }
                        else{
                            curGametype = RoomType.valueOf(snapshot.get("gameType").toString())
                            startRoomTypeGame()
                            updateGameState()
                        }


                    }
                }
            }
    }

    private fun startGame(kelime : String,timelimit : Int) {
        // Oyun başlatma ekranına geç
        if(!isGameStarted){
            isGameStarted = true
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("GAME_ID", gameId)

intent.putExtra("TIME_LIMIT",timelimit)
intent.putExtra("WORD",kelime)
checkUserState(FirebaseAuth.getInstance().currentUser?.uid.toString())
        Log.d("CreateGameActivity", "CreategamewaitingActivity 65 calling GameActivity Game ID: $gameId")
        startActivity(intent)
        finish()
    }}
    private fun startRoomTypeGame(){
        // Oyun başlatma ekranına geç
        if(!isGameStarted){
            isGameStarted = true
        val intent = Intent(this, PickWordActivity::class.java)
        intent.putExtra("GAME_ID", gameId)
            intent.putExtra("gameType",curGametype )
            checkUserState(FirebaseAuth.getInstance().currentUser?.uid.toString())
        Log.d("CreateGameActivity", "CreategamewaitingActivity 91 calling PickWordActivity Game ID: $gameId")
        startActivity(intent)
        finish()
    }}


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
