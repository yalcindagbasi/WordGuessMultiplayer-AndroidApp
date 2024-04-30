package com.example.wordgameproject

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wordgameproject.Models.GameModel
import com.example.wordgameproject.Models.GameStatus
import com.example.wordgameproject.Models.RoomType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.random.Random

import kotlinx.coroutines.launch

class PickWordActivity : AppCompatActivity() {

    private lateinit var editTextWord: EditText
    private lateinit var buttonSubmit: Button
    private var letterCount : Int =0
    private var template : String? = null
    private var countDownTimer: CountDownTimer? = null
    private lateinit var gameType : RoomType
    private lateinit var gameId : String
    private lateinit var lblCountDown : TextView
    private  var opponentWordListener : ListenerRegistration ?=null
    private var gameModel : GameModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickword)
        val lblTemplate: TextView = findViewById(R.id.lblTemplate)

        editTextWord = findViewById(R.id.et_pickedWord)
        buttonSubmit = findViewById(R.id.confirmButton)
        gameType= intent.getSerializableExtra("gameType") as RoomType
        gameId = intent.getStringExtra("GAME_ID").toString()
        GameData.fetchGameModel(gameId)
        lblCountDown = findViewById(R.id.lblCountDown)

        checkGameType()
        lblTemplate.text = if(template!=null && template!!.isNotEmpty()) template else "_".repeat(letterCount)
            buttonSubmit.setOnClickListener {
            val word = editTextWord.text.toString()
            if(word.length!=letterCount){
                Toast.makeText(this, "Lütfen $letterCount harfli bir kelime girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (word.isNotEmpty()) {
                if (template !=null && !doesWordMatchTemplate(word, template!!)) {
                    Toast.makeText(this, "Kelime, belirtilen şablona uymalıdır.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else{
                    GlobalScope.launch {
                        val isWordValid = checkWord(word)
                        withContext(Dispatchers.Main) {
                            if (!isWordValid) {
                                Toast.makeText(this@PickWordActivity, "Seçtiğiniz kelime sözlükte bulunmayan bir kelime olamaz.", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(this@PickWordActivity, "Kelime kabul edildi", Toast.LENGTH_SHORT).show()
                                buttonSubmit.isEnabled =false
                                editTextWord.setText(word)
                                lblTemplate.text = word
                                editTextWord.isEnabled = false
                                val textview= findViewById<TextView>(R.id.textView2)
                                textview.text= "Rakip Bekleniyor..."

                                // Get the current user's ID
                                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                                // Get the Firestore instance
                                val db = FirebaseFirestore.getInstance()
                                val gameModel = GameData.gameModel.value

                                // Determine which player's word to update based on the current user's ID
                                val wordField = if (gameModel!!.player1Id == currentUserId) "player2word" else "player1word"

                                // Update the word in the Firestore
                                db.collection("games").document(gameId)
                                    .update(wordField, word)
                                    .addOnSuccessListener {
                                        Log.d("PickWordActivity", "Word successfully written!")

                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("PickWordActivity", "Error writing word", e)
                                    }

                                // Add a snapshot listener to the game document
                                opponentWordListener = db.collection("games").document(gameId)
                                    .addSnapshotListener { snapshot, e ->
                                        if (e != null) {
                                            Log.w("PickWordActivity", "Listen failed.", e)
                                            return@addSnapshotListener
                                        }

                                        if (snapshot != null && snapshot.exists()) {
                                            val player1word = snapshot.getString("player1word")
                                            val player2word = snapshot.getString("player2word")

                                            // Check if both player1word and player2word are filled
                                            if (!player1word.isNullOrEmpty() && !player2word.isNullOrEmpty() && player1word!= "" && player2word!= "") {
                                                // Transition to the game stage
                                                val intent = Intent(this@PickWordActivity, GameActivity::class.java)
                                                intent.putExtra("GAME_ID", gameId)
                                                intent.putExtra("WORD", if(gameModel.player1Id == currentUserId) player1word else player2word)
                                                intent.putExtra("TIME_LIMIT", gameModel.timeLimit)
                                                opponentWordListener?.remove()
                                                startActivity(intent)
                                                finish()
                                            }
                                        } else {
                                            Log.d("PickWordActivity", "Current data: null")
                                        }
                                    }
                            }
                        }
                    }
                }

            } else {
                Toast.makeText(this, "Lütfen bir kelime girin", Toast.LENGTH_SHORT).show()
            }
        }
        if (gameId != null) {
            FirebaseFirestore.getInstance().collection("games")
                .document(gameId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val gameModel = document.toObject(GameModel::class.java)
                        if (gameModel != null) {
                            GameData.saveGameModel(gameModel)
                        }
                        // Kelimeyi al ve word değişkenine at
                    }
                }
        }
        GameData.fetchGameModel(gameId!!)
        GameData.gameModel.observe(this) { gameModel ->
            this.gameModel = gameModel
            // Eğer oyun durumu FINISHED ise, oyunu bitir ve kazananın ismini göster
            if (gameModel.gameState == GameStatus.FINISHED) {

                endGame(gameModel.winnerId)
            }
        }

        startTimer()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                println("Kalan süre: $secondsRemaining saniye")
                lblCountDown.text= secondsRemaining.toString()

            }

            override fun onFinish() {
                println("Süre doldu!")
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val currentPlayerWord = if (gameModel?.player1Id == currentUserId) gameModel?.player1word else gameModel?.player2word
                val opponentWord = if (gameModel?.player1Id == currentUserId) gameModel?.player2word else gameModel?.player1word

                if (currentPlayerWord.isNullOrEmpty() || currentPlayerWord == "") {
                    // Kelime gönderen taraf kelime göndermedi, bu yüzden karşı taraf oyunu kazandı
                    endGameWithCurrentPlayerAsWinner()

                } else if (opponentWord.isNullOrEmpty() || opponentWord == "") {
                    // Karşı taraf kelime göndermedi, bu yüzden kelime gönderen taraf oyunu kazandı
                    endGameWithOpponentAsWinner()
                }
            }
        }.start()
    }


    private fun endGameWithCurrentPlayerAsWinner() {
        gameModel?.let {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

            it.gameState = GameStatus.FINISHED
            it.winnerId = currentUserId

            FirebaseFirestore.getInstance().collection("games")
                .document(it.gameId)
                .set(it)
                .addOnSuccessListener {
                    // Güncelleme başarılı ise bir işlem yapma
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@PickWordActivity, "Oyun durumu güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Oyun bittiğinde anasayfaya dönme butonu içeren bir AlertDialog oluştur
            AlertDialog.Builder(this)
                .setTitle("Oyun Bitti")
                .setMessage("Rakip kelime girmediği için Oyunu kazandınız!")
                .setPositiveButton("Anasayfaya Dön") { _, _ ->
                    // Anasayfaya dönme Intent'i oluştur
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }

    private fun endGameWithOpponentAsWinner() {
        gameModel?.let {
            it.gameState = GameStatus.FINISHED
            it.winnerId = if (it.player1Id == FirebaseAuth.getInstance().currentUser?.uid) it.player2Id else it.player1Id

            FirebaseFirestore.getInstance().collection("games")
                .document(it.gameId)
                .set(it)
                .addOnSuccessListener {
                    // Güncelleme başarılı ise bir işlem yapma
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@PickWordActivity, "Oyun durumu güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Oyun bittiğinde anasayfaya dönme butonu içeren bir AlertDialog oluştur
            AlertDialog.Builder(this)
                .setTitle("Oyun Bitti")
                .setMessage("Kelime girmediğiniz için  oyunu kaybettiniz.")
                .setPositiveButton("Anasayfaya Dön") { _, _ ->
                    // Anasayfaya dönme Intent'i oluştur
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }
    private fun stopTimer() {
        countDownTimer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
    suspend fun checkWord(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            val url = "https://sozluk.gov.tr/gts_id?id=$word"
            val bodyText = URL(url).readText()
            bodyText != """{"error":"Sonuç bulunamadı"}"""
        }
    }
    private fun doesWordMatchTemplate(word: String, template: String): Boolean {
        if (word.length != template.length) {
            return false
        }

        for (i in word.indices) {
            if (template[i] != '_' && !word[i].equals(template[i], ignoreCase = true)) {
                return false
            }
        }

        return true
    }
    fun createTemplate() {
        // '_' karakteri ile dolu bir liste oluştur
        val templateChars = MutableList(letterCount) { '_' }

        // Rastgele bir konum ve harf seç
        val randomPosition = Random.nextInt(letterCount)
        val alfabe = ('A'..'Z').filter { it != 'Q' && it != 'W' && it != 'X' && it != 'J' && it!= 'G'}
        val randomChar = alfabe.random()

        // Rastgele seçilen konuma rastgele seçilen harfi yerleştir
        templateChars[randomPosition] = randomChar

        // Listeyi bir stringe dönüştür
        template = templateChars.joinToString("")

        // Şablonu güncelle
        val lblTemplate: TextView = findViewById(R.id.lblTemplate)
        lblTemplate.text = template
    }
    fun checkGameType(){
        when(gameType){
            RoomType.type1length4 ->{
                letterCount=4
                createTemplate()
            }
            RoomType.type1length5 ->{
                letterCount=5
                createTemplate()
            }
            RoomType.type1length6 ->{
                letterCount=6
                createTemplate()
            }
            RoomType.type1length7 ->{
                letterCount=7
                createTemplate()
            }
            RoomType.type2length4 ->{
                letterCount=4
            }
            RoomType.type2length5 ->{
                letterCount=5
            }
            RoomType.type2length6 ->{
                letterCount=6
            }
            RoomType.type2length7 ->{
                letterCount=7
            }

            RoomType.type0 -> TODO()
            RoomType.NOT -> TODO()
        }
    }



    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Oyundan çıkmak istediğinize emin misiniz?")
            .setMessage("Ayrılırsanız rakip kazanacak.")
            .setPositiveButton("Evet") { _, _ ->
                endGameWithOpponentAsWinner()
                super.onBackPressed()
            }
            .setNegativeButton("Hayır", null)
            .show()
    }



    private fun endGame(winnerId: String) {
        // Tahmin etme işlemini durdur


        // Kazananın ismini ekranda göster
        if(winnerId==FirebaseAuth.getInstance().currentUser?.uid){
            Toast.makeText(this, "Tebrikler! Oyunu kazandın!", Toast.LENGTH_SHORT).show()
        }
        else if(winnerId== "DRAW")
        {
            Toast.makeText(this, "Oyun berabere bitti!", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Oyunu kaybettin!", Toast.LENGTH_SHORT).show()
        }

        stopTimer()

    }
}