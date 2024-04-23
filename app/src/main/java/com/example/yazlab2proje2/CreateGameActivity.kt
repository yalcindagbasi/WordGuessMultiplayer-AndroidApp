package com.example.yazlab2proje2
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.yazlab2proje2.Models.GameModel
import com.example.yazlab2proje2.Models.GameStatus
import com.example.yazlab2proje2.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.InputStream
import java.util.*

public class CreateGameActivity : AppCompatActivity() {


    private lateinit var letterCountEditText: EditText
    private lateinit var timeEditText: EditText

    private lateinit var firebaseAuth: FirebaseAuth
    private var kelime: String = ""
    private var letterCount: Int = 0

    private var gameModel : GameModel? = null

    lateinit var btnCreateGame : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creategame)
        firebaseAuth = FirebaseAuth.getInstance()

        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            Utils.updateUserState(userId, UserState.ONLINE)
        }
        GameData.gameModel.observe(this){
            gameModel=it
        }

        letterCountEditText = findViewById(R.id.txtfield_LetterCount)



        firebaseAuth = FirebaseAuth.getInstance()


        btnCreateGame = findViewById<Button>(R.id.btn_CreateGame)
        val player2Id = intent.getStringExtra("player2Id")
        if(player2Id != null){
            Log.d("player2Id",player2Id?:"")
            val constraintLayout: ConstraintLayout = findViewById(R.id.layout) // veya uygun konteyneri bul

            // ConstraintLayout içindeki tüm çocuk görünümleri al
            for (i in 0 until constraintLayout.childCount) {
                val view = constraintLayout.getChildAt(i)
                view.visibility = View.INVISIBLE // veya View.GONE
            }
            val gameId = generateGameId() // Oyun için benzersiz bir kimlik oluştur
            timeEditText = findViewById(R.id.txtfield_Time)
            val time : Int = timeEditText.text.toString().toInt()
            if(intent.getStringExtra("lettercount")!=null){
                letterCountEditText.setText(intent.getStringExtra("lettercount"))
            }
            letterCount = letterCountEditText.text.toString().toInt()
            createGame(letterCount,time,gameId,firebaseAuth.currentUser?.uid ?: "", player2Id?:"")

        }
        btnCreateGame.setOnClickListener{
            val gameId = generateGameId() // Oyun için benzersiz bir kimlik oluştur
            timeEditText = findViewById(R.id.txtfield_Time)
            val time : Int = timeEditText.text.toString().toInt()
            if(intent.getStringExtra("lettercount")!=null){
                letterCountEditText.setText(intent.getStringExtra("lettercount"))
            }
            letterCount = letterCountEditText.text.toString().toInt()
            createGame(letterCount,time,gameId,firebaseAuth.currentUser?.uid ?: "", "")
        }

        //ANASAYFA BUTONU
        val btnHomePage = findViewById<Button>(R.id.btn_HomePage)
        btnHomePage.setOnClickListener {
            finish()
        }

        //HESAPTAN ÇIKIŞ YAP BUTONU
        val btnSignOut = findViewById<Button>(R.id.btn_Logout)
        btnSignOut.setOnClickListener {
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
    }

    private fun createGame(letterCount: Int,time: Int,gameId : String,player1Id: String,player2Id: String ) {

        btnCreateGame.setEnabled(false)
        if (letterCount==null || time==null) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }





        // Oluşturulan oyunu veritabanına kaydet


        if(player2Id == ""){
            kelime = generateRandomWord(letterCount) // Harf sayısına göre rastgele bir kelime
            GameData.saveGameModel(
                GameModel(
                    gameId= gameId,
                    player1Id = firebaseAuth.currentUser?.uid ?: "", // Oyunu oluşturan kullanıcının UID'si
                    wordLength = letterCount.toInt(),
                    gameState = GameStatus.CREATED, // Oyun henüz başlamadı
                    timeLimit = time, // Kullanıcının girdiği süre
                    word = kelime // Harf sayısına göre rastgele bir kelime
                )
            )
        }else{
            this.letterCount =intent.getStringExtra("lettercount")?.toInt()?:0
            kelime = generateRandomWord(letterCount)
            GameData.saveGameModel(
                GameModel(
                    gameId= gameId,
                    player1Id = firebaseAuth.currentUser?.uid ?: "", // Oyunu oluşturan kullanıcının UID'si
                    player2Id = player2Id,
                    wordLength = letterCount.toInt(),
                    gameState = GameStatus.CREATED, // Oyun henüz başlamadı
                    timeLimit = time, // Kullanıcının girdiği süre
                    word = kelime // Harf sayısına göre rastgele bir kelime
                )
            )

        }

            Firebase.firestore.collection("games")
                .document(gameId)
                .get()
                .addOnSuccessListener {
                    val model= it?.toObject(GameModel::class.java)
                    if(model!=null){
                        Toast.makeText(this, "Oyun oluşturuldu", Toast.LENGTH_SHORT).show()
                        // Oyun ID'sini diğer oyuncuya göndermek için Intent'i ayarla
                        val intent = Intent(this, CreateGameWaitingActivity::class.java)
                        intent.putExtra("GAME_ID", gameId)
                        Log.d("gameId","******CreateGameActivity 154 gameID:"+gameId)
                        intent.putExtra("WORD", kelime)
                        intent.putExtra("TIME", time)
                        intent.putExtra("LETTERCOUNT", letterCount)
                        if(player2Id != ""){
                            // player2Id'ye sahip kullanıcının veritabanındaki bilgilerini güncelle
                            FirebaseFirestore.getInstance().collection("users")
                                .document(player2Id)
                                .update(mapOf("gameId" to gameId, "state" to UserState.INGAME))
                                .addOnSuccessListener {
                                    // Güncelleme başarılı
                                }
                                .addOnFailureListener { e ->
                                    // Güncelleme başarısız
                                }
                        }

                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this, "Oyun oluşturulamadı", Toast.LENGTH_SHORT).show()
                        btnCreateGame.setEnabled(true)
                    }

            }

    }

    // Oyun için benzersiz bir kimlik oluştur
    private fun generateGameId(): String {
        val random = Random()
        val gameId = random.nextInt(10000) // 0 ile 9999 arasında rastgele bir sayı oluşturur
        return String.format("%04d", gameId) // 4 haneli olacak şekilde formatlanır
    }
    private fun generateRandomWord(letterCount: Int): String {

        val context = this
        var inputStream: InputStream
        when (letterCount) {
            4 -> {
                inputStream=context.resources.openRawResource(R.raw.length_4_words)
            }
            5 -> {
                inputStream=context.resources.openRawResource(R.raw.length_5_words)
            }
            6 -> {
                inputStream=context.resources.openRawResource(R.raw.length_6_words)
            }
            7 -> {
                inputStream=context.resources.openRawResource(R.raw.length_7_words)
            }
            else -> {
                return "Kelime bulunamadı"
            }
        }

        val words = inputStream.bufferedReader().use { it.readText() }.split("\n")
        val random = Random()
        val word = words[random.nextInt(words.size)].trim()
        Log.d("harf sayısı",letterCount.toString())
        Log.d("kelime",word)
        return if (word.isNotEmpty()) {
            word
        } else {
            "Dosyada kelime bulunamadı"
        }
    }
}
