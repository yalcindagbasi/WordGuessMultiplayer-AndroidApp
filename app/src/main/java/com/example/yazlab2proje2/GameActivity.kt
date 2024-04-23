package com.example.yazlab2proje2

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yazlab2proje2.Models.GameModel
import com.example.yazlab2proje2.Models.GameStatus
import com.example.yazlab2proje2.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class GameActivity : AppCompatActivity() {
    private lateinit var kelime: String
    private lateinit var gameLayout: LinearLayout
    private lateinit var guessInput: EditText
    private lateinit var backButton: Button // Anasayfaya dönmek için buton
    private var gameModel : GameModel? = null
    private var kutuSayisi: Int = 0
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var timerTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val gameId = intent.getStringExtra("GAME_ID")
        val gameIdlbl= findViewById<TextView>(R.id.lblgameIDd)
        gameIdlbl.text=gameId
        kelime = intent.getStringExtra("WORD").toString()
        val lblworddebug= findViewById<TextView>(R.id.lblworddebug)
        //lblworddebug.text=kelime
        Utils.updateUserState(FirebaseAuth.getInstance().currentUser!!.uid, UserState.INGAME)
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
                lblworddebug.text=kelime
                endGame(gameModel.winnerId)
            }
        }
        timerTextView = findViewById(R.id.timerTextView)
        val timeLimit = intent.getIntExtra("TIME_LIMIT", 60)
        startCountDown(timeLimit)

        // Oyun durumunu sürekli olarak dinle
        // Oyun durumunu sürekli olarak dinle


        if (gameId != null) {
            FirebaseFirestore.getInstance().collection("games")
                .document(gameId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Hata durumunda hata mesajı göster
                        Toast.makeText(this@GameActivity, "Bir hata oluştu: ${exception.message}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val gameState = snapshot.getString("gameState")
                        if (gameState == "FINISHED") {
                            // Oyun durumu FINISHED ise, oyunu bitir ve kazananın ismini göster
                            val winnerId = snapshot.getString("winnerId")
                            if (winnerId != null) {
                                endGame(winnerId)
                            }
                        }
                    }
                }
        }



        gameLayout = findViewById(R.id.gameLayout)
        guessInput = findViewById(R.id.guessInput)
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { goToMain() }
        val guessButton: Button = findViewById(R.id.guessButton)
        guessButton.setOnClickListener {
            if(guessInput.text.length!= kelime.length){
                Toast.makeText(this, "Tahmininiz ${kelime.length} harfli olmalıdır.", Toast.LENGTH_SHORT).show()
            }
            /* internetten kelime kontrolü
            else {
    GlobalScope.launch {
        val isWordValid = checkWord(kelime)
        withContext(Dispatchers.Main) {
            if (!isWordValid) {
                Toast.makeText(this@GameActivity, "Tahmininiz sözlükte bulunmayan bir kelime olamaz.", Toast.LENGTH_SHORT).show()
            }
            else{
                tahminEt()
                }
        }
    }
}
            */
            else if (!checkIfWord(kelime)){
                Toast.makeText(this, "Tahmininiz sözlükte bulunmayan bir kelime olamaz.", Toast.LENGTH_SHORT).show()
            }
            else{
                tahminEt()
            }
             }

        kutuSayisi = kelime.length

        kelimeyiGoster()
    }

    private fun kelimeyiGoster() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        val rowLayout = olusturSatir()
        for (i in 0 until kutuSayisi) {
            val textView = olusturKutu()
            textView.layoutParams.width = screenWidth / kutuSayisi
            rowLayout.addView(textView)
        }
        gameLayout.addView(rowLayout)
    }

    private fun olusturSatir(): LinearLayout {
        val rowLayout = LinearLayout(this)
        rowLayout.orientation = LinearLayout.HORIZONTAL
        rowLayout.gravity = LinearLayout.HORIZONTAL
        return rowLayout
    }

    private fun olusturKutu(): TextView {
        val textView = TextView(this)
        textView.text = ""
        textView.setBackgroundResource(R.drawable.correct_background)
        textView.setTextColor(Color.BLACK)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        textView.setTypeface(null, Typeface.BOLD)
        textView.setPadding(24, 24, 24, 24)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 8, 8, 8)
        layoutParams.weight = 1f
        textView.layoutParams = layoutParams

        textView.gravity = Gravity.CENTER
        return textView
    }



    private fun tahminEt() {
        val tahmin = guessInput.text.toString()
        val rowLayout = olusturSatir()

        for (i in 0 until kutuSayisi) {
            val textView = olusturKutu()
            val harf = if (i < kelime.length) kelime[i] else '_'
            val tahminHarfi = if (i < tahmin.length) tahmin[i].toUpperCase() else '_'
            textView.text = tahminHarfi.toString()
            textView.setTextColor(Color.BLACK)
            textView.setBackgroundColor(if (tahminHarfi.equals(harf,ignoreCase = true)) Color.parseColor("#4CAF50") else if (kelime.contains(tahminHarfi,ignoreCase = true)) Color.parseColor("#FF5722") else Color.GRAY )
            rowLayout.addView(textView)
        }
        gameLayout.addView(rowLayout)
        if (tahmin.equals(kelime, ignoreCase = true)) {
            //Toast.makeText(this, "Tebrikler! Oyunu kazandınız!", Toast.LENGTH_SHORT).show()
            guessInput.isEnabled = false // EditText'i devre dışı bırak
            val guessButton: Button = findViewById(R.id.guessButton)
            guessButton.isEnabled = false // Button'u devre dışı bırak
            backButton.visibility= View.VISIBLE

            // Oyunu bitir ve kazananın ismini ekranda göster
            gameModel?.let {
                it.gameState = GameStatus.FINISHED
                it.winnerId = if (it.player1Id == FirebaseAuth.getInstance().currentUser?.uid) it.player1Id else it.player2Id
                FirebaseFirestore.getInstance().collection("games")
                    .document(it.gameId)
                    .set(it)
                    .addOnSuccessListener {
                        // Güncelleme başarılı ise bir işlem yapma
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@GameActivity, "Oyun durumu güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        guessInput.text.clear()

        // Tahmin edilen kelimeyi ilgili oyuncunun tahminler dizisine ekle
        gameModel?.let {
            val currentPlayer = if (it.player1Id == FirebaseAuth.getInstance().currentUser?.uid) "player1Guesses" else "player2Guesses"
            FirebaseFirestore.getInstance().collection("games")
                .document(it.gameId)
                .update(currentPlayer, FieldValue.arrayUnion(tahmin))
                .addOnSuccessListener {
                    // Güncelleme başarılı ise bir işlem yapma
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@GameActivity, "Tahminler güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // GameActivity'yi kapat
    }
    // Oyunu bitirme ve kazananın ismini gösterme işlemlerini gerçekleştiren metot
    private fun endGame(winnerId: String) {
        // Tahmin etme işlemini durdur
        guessInput.isEnabled = false // EditText'i devre dışı bırak
        val guessButton: Button = findViewById(R.id.guessButton)
        guessButton.isEnabled = false // Button'u devre dışı bırak
        backButton.visibility= View.VISIBLE

        // Kazananın ismini ekranda göster
        val winnerName = if (winnerId == FirebaseAuth.getInstance().currentUser?.uid) "Sen kazandın!" else "Rakip kazandı!"
        stopCountDown()
        Toast.makeText(this, winnerName, Toast.LENGTH_LONG).show()
    }
    private fun stopCountDown() {
        countDownTimer.cancel()
    }
    private fun startCountDown(timeLimit: Int) {
        Log.d("GameActivity", "startCountdown called") // Bu log mesajını ekleyin

        countDownTimer = object : CountDownTimer(timeLimit * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = "$secondsRemaining"
            }

            override fun onFinish() {
                // Süre dolduğunda oyunu berabere olarak bitir
                gameModel?.let {
                    it.gameState = GameStatus.FINISHED
                    it.winnerId = "DRAW"
                    FirebaseFirestore.getInstance().collection("games")
                        .document(it.gameId)
                        .set(it)
                }
            }
        }.start()
    }
    //lokalden kelime kontrolü
    private fun checkIfWord(guess :String): Boolean {

        val context = this
        var inputStream: InputStream
        when (guess.length) {
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
                return false
            }
        }

        val words = inputStream.bufferedReader().use { it.readText() }.split("\n")
        words.forEach() {
            if(it.equals(guess,ignoreCase = true)){
                return true
            }
        }
        return false
    }
    //internetten kelime kontrolü
    suspend fun checkWord(word: String): Boolean {
        return withContext(Dispatchers.IO) {
            val url = "https://sozluk.gov.tr/gts_id?id=$word"
            val bodyText = URL(url).readText()
            bodyText != """{"error":"Sonuç bulunamadı"}"""
        }
    }

}