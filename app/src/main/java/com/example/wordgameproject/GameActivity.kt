package com.example.wordgameproject

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.wordgameproject.Models.GameModel
import com.example.wordgameproject.Models.GameStatus
import com.example.wordgameproject.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
    private var guessCount : Int = 0
    private lateinit var guessButton: Button
    private lateinit var player2Id :String
    private var gamelistener: ListenerRegistration? = null
    private var opponentleft : Boolean =false
    private var gamedoclistener : ListenerRegistration? = null
    private var gameId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        Log.d("GameActivity", "GameActivity 69 onCreate")
        gameId = intent.getStringExtra("GAME_ID")
        val gameIdlbl= findViewById<TextView>(R.id.lblgameIDd)
        gameIdlbl.text=gameId
        kelime = intent.getStringExtra("WORD").toString()
        val lblworddebug= findViewById<TextView>(R.id.lblworddebug)
        lblworddebug.isVisible=false
        guessCount=kelime.length
        //lblworddebug.text=kelime
        Utils.updateUserState(FirebaseAuth.getInstance().currentUser!!.uid, UserState.INGAME)
        if (gameId != null) {
            FirebaseFirestore.getInstance().collection("games")
                .document(gameId!!)
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


            }
        }
        timerTextView = findViewById(R.id.timerTextView)
        val timeLimit = intent.getIntExtra("TIME_LIMIT", 60)
        startCountDown(timeLimit)

        // Oyun durumunu sürekli olarak dinle
        // Oyun durumunu sürekli olarak dinle

        Log.d("GameActivity", "GameActivity CHECK 94 gameId: $gameId")
        if (gameId != null) {
            gamelistener = FirebaseFirestore.getInstance().collection("games")
                .document(gameId!!)
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
                                gamelistener?.remove()
                                lblworddebug.isVisible=true
                                lblworddebug.text=kelime
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
        guessButton = findViewById(R.id.guessButton)
        guessButton.setOnClickListener {
            if (guessInput.text.length != kelime.length) {
                Toast.makeText(
                    this,
                    "Tahmininiz ${kelime.length} harfli olmalıdır.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                GlobalScope.launch {
                    val tahmin = guessInput.text.toString()
                    val isWordValid = checkWord(tahmin)
                    withContext(Dispatchers.Main) {
                        if (isWordValid) {
                            tahminEt()
                        } else {
                            Toast.makeText(this@GameActivity, "Kelime değil", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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
            stopCountDown()
            gameModel?.let { game ->
                val currentPlayer = if (game.player1Id == FirebaseAuth.getInstance().currentUser?.uid) "player1Guesses" else "player2Guesses"
                FirebaseFirestore.getInstance().collection("games")
                    .document(game.gameId)
                    .update(currentPlayer, FieldValue.arrayUnion(tahmin))
                    .addOnSuccessListener {
                        // Güncelleme başarılı ise bir işlem yapma
                        game.gameState = GameStatus.FINISHED
                        game.winnerId = if (game.player1Id == FirebaseAuth.getInstance().currentUser?.uid) game.player1Id else game.player2Id
                        FirebaseFirestore.getInstance().collection("games")
                            .document(game.gameId)
                            .set(game)
                            .addOnSuccessListener {
                                // Güncelleme başarılı ise bir işlem yapma
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@GameActivity, "Oyun durumu güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@GameActivity, "Tahminler güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
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
        if(guessCount==1){
            Toast.makeText(this, "Tahmin hakkınız kalmadı.", Toast.LENGTH_SHORT).show()
            guessButton.isEnabled=false
        }
        else{

            guessCount--
            Toast.makeText(this, "Kalan tahmin hakkınız: $guessCount", Toast.LENGTH_SHORT).show()
        }
    }
    fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        Log.d("GameActivity", "GameActivity 259 calling MainActivity")
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


        stopCountDown()

        // Firestore instance'ını al
       updateScoresAndDisplay()


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

                    Toast.makeText(this@GameActivity, "FINISHED 291 Süre doldu. Oyun berabere bitti.", Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        stopCountDown()
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

    private fun endGameWithOpponentAsWinner() {
        opponentleft=true
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
                    Toast.makeText(this@GameActivity, "Oyun durumu güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    fun calculateScore(guess: String, word: String): Int {
        var score = 0
        for (i in guess.indices) {
            if (i < word.length) {
                if (guess[i].equals(word[i], ignoreCase = true)) {
                    score += 10
                } else if (word.contains(guess[i], ignoreCase = true)) {
                    score += 5
                }
            }
        }
        return score
    }

    fun updateScoresAndDisplay() {
        // Son tahmini al

        val latestGuess = if (gameModel?.player1Id == FirebaseAuth.getInstance().currentUser?.uid) {
            if (gameModel?.player1Guesses?.isNotEmpty() == true) gameModel?.player1Guesses?.last() else null
        } else {
            if (gameModel?.player2Guesses?.isNotEmpty() == true) gameModel?.player2Guesses?.last() else null
        }

        val opponentGuess = if (gameModel?.player1Id == FirebaseAuth.getInstance().currentUser?.uid) {
            if (gameModel?.player2Guesses?.isNotEmpty() == true) gameModel?.player2Guesses?.last() else null
        } else {
            if (gameModel?.player1Guesses?.isNotEmpty() == true) gameModel?.player1Guesses?.last() else null
        }
        var score=0
        if (latestGuess!=null){

            // Puanı hesapla
            score = latestGuess?.let { calculateScore(it, kelime) }!!

            // Puanı veritabanına yaz
            val gameId = gameModel?.gameId
            val currentPlayer = if (gameModel?.player1Id == FirebaseAuth.getInstance().currentUser?.uid) "player1score" else "player2score"
            FirebaseFirestore.getInstance().collection("games")
                .document(gameId!!)
                .update(currentPlayer, score)
                .addOnSuccessListener {
                    // Güncelleme başarılı ise bir işlem yapma
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@GameActivity, "Puan güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Rakibin son tahmini ve kelimesini al
        }
        var opponentScore=0
        if(opponentGuess!=null){

            val opponentWord = if (gameModel?.player1Id == FirebaseAuth.getInstance().currentUser?.uid) gameModel?.player2word else gameModel?.player1word

            // Rakibin skorunu hesapla
            opponentScore = calculateScore(opponentGuess ?: "", opponentWord ?: "")

            // Rakibin skorunu veritabanına yaz
            val opponentPlayer = if (gameModel?.player1Id == FirebaseAuth.getInstance().currentUser?.uid) "player2score" else "player1score"
            FirebaseFirestore.getInstance().collection("games")
                .document(gameModel?.gameId ?: "")
                .update(opponentPlayer, opponentScore)
                .addOnSuccessListener {
                    // Güncelleme başarılı ise bir işlem yapma
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@GameActivity, "Rakibin puanı güncellenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }


        // Puanları ekrana yaz
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if(score!! > opponentScore) "Kazandın" else if(score < opponentScore) "Kaybettin" else "Berabere")
        builder.setMessage("Skorun: $score, Rakibinin Skoru: $opponentScore")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }


}