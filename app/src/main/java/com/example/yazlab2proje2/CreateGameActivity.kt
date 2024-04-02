package com.example.yazlab2proje2
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.yazlab2proje2.Models.GameModel
import com.example.yazlab2proje2.Models.GameStatus
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

public class CreateGameActivity : AppCompatActivity() {


    private lateinit var letterCountEditText: EditText
    private lateinit var timeEditText: EditText

    private lateinit var firebaseAuth: FirebaseAuth
    private var kelime: String = ""

    private var gameModel : GameModel? = null

    lateinit var btnCreateGame : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creategame)

        GameData.gameModel.observe(this){
            gameModel=it
        }
        GameData.fetchGameModel()
        letterCountEditText = findViewById(R.id.txtfield_LetterCount)
        timeEditText = findViewById(R.id.txtfield_Time)

        firebaseAuth = FirebaseAuth.getInstance()


        btnCreateGame = findViewById<Button>(R.id.btn_CreateGame)

        btnCreateGame.setOnClickListener{
            createGame()
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
            startActivity(intent)
            finish()
        }

        //ÇIKIŞ YAP BUTONU
        val buttonLogout = findViewById<Button>(R.id.btn_ExitGame)
        buttonLogout.setOnClickListener {
            finish()
        }
    }

    private fun createGame() {
        val letterCount = letterCountEditText.text.toString().toInt()
        val time : Int = timeEditText.text.toString().toInt()
        btnCreateGame.setEnabled(false)
        if (letterCount==null || time==null) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        val gameId = generateGameId() // Oyun için benzersiz bir kimlik oluştur




        // Oluşturulan oyunu veritabanına kaydet

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
                        intent.putExtra("WORD", kelime)
                        intent.putExtra("TIME", time)
                        intent.putExtra("LETTERCOUNT", letterCount)
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
        // TODO: Gerçek bir kelime listesi kullanın
        val words = listOf("apple", "banana", "cherry", "date", "elderberry")
        return words.filter { it.length == letterCount }.random()
    }
}
