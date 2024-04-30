package com.example.wordgameproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordgameproject.Models.GameModel
import com.example.wordgameproject.Models.GameStatus
import com.example.wordgameproject.Models.RoomType
import com.example.wordgameproject.Models.UserState
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
    private lateinit var gameType : RoomType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joingame)

        gameId = intent.getStringExtra("GAME_ID").toString()
        firebaseFirestore = FirebaseFirestore.getInstance()
        val player2Id= intent.getStringExtra("player2Id").toString()
        val lbl_gameID = findViewById<TextView>(R.id.lbl_GameID)
        lbl_gameID.text= gameId
        val roomTypeExtra = intent.getSerializableExtra("roomType")
        if (roomTypeExtra != null) {
            gameType = roomTypeExtra as RoomType
        }
        else{
            gameType= RoomType.type0
        }
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
                    gameType= gameModel.gameType

                    if(gameType == RoomType.type0){
                        val kelime = snapshot.getString("player1word")
                        val timelimit = snapshot.getLong("timeLimit")!!.toInt()
                        if (kelime != null) {
                            gameListener?.remove()
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
                    else{
                        gameListener?.remove()
                        startRoomTypeGame()
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
        Log.d("JoinGameActivity", "JoinGameActivity 94 calling GameActivity Game ID: $gameId")
        startActivity(intent)
        finish()
    }
    private fun startRoomTypeGame(){
        // Oyun başlatma ekranına geç
        val intent = Intent(this, PickWordActivity::class.java)
        intent.putExtra("GAME_ID", gameId)
        intent.putExtra("gameType",gameType )
        Log.d("JoinGameActivity", "JoinGameActivity 116 calling PickWordActivity Game ID: $gameId")
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
    override fun onResume() {
        super.onResume()

        if (isTaskRoot) {
            // Activity zaten çalışıyorsa, yeni bir Activity başlatmak yerine mevcut Activity'yi kullanın
        } else {
            // Activity zaten çalışmıyorsa, yeni bir Activity başlatın
        }
    }
}
