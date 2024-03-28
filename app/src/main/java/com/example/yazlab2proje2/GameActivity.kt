//package com.example.yazlab2proje2
//
//import android.graphics.Color
//import android.os.Bundle
//import android.view.Gravity
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.LinearLayout
//import android.widget.TableLayout
//import android.widget.TableRow
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//
//class GameActivity : AppCompatActivity() {
//    private lateinit var textViewWordToGuess: TextView
//    private lateinit var linearLayoutWordGuess: LinearLayout
//    private lateinit var buttonSubmitGuess: Button
//    private lateinit var textViewFeedback: TextView
//    private lateinit var editTextWordGuess: EditText
//    private lateinit var tableLayoutWordBoxes: TableLayout
//
//    private val wordToGuess = "kelam" // Tahmin edilecek kelime
//    private val guessedLetters = BooleanArray(wordToGuess.length) { false }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_game)
//
//        textViewWordToGuess = findViewById(R.id.textViewWordToGuess)
//        linearLayoutWordGuess = findViewById(R.id.linearLayoutWordGuess)
//        buttonSubmitGuess = findViewById(R.id.buttonSubmitGuess)
//        textViewFeedback = findViewById(R.id.textViewFeedback)
//        editTextWordGuess = findViewById(R.id.editTextWordGuess)
//        tableLayoutWordBoxes = findViewById(R.id.tableLayoutWordBoxes)
//
//
//        textViewWordToGuess.text = "Tahmin edilecek kelime:"
//
//        for (i in wordToGuess.indices) {
//            val textViewLetter = TextView(this)
//            textViewLetter.text = "_"
//            textViewLetter.textSize = 24f
//            textViewLetter.gravity = Gravity.CENTER
//            textViewLetter.setTextColor(Color.BLACK)
//            textViewLetter.setBackgroundResource(R.drawable.letter_box)
//            val params = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            params.setMargins(4, 0, 4, 0)
//            textViewLetter.layoutParams = params
//            linearLayoutWordGuess.addView(textViewLetter)
//        }
//
//        buttonSubmitGuess.setOnClickListener {
//            evaluateGuess()
//        }
//    }
//
//    private fun evaluateGuess() {
//        val guess = editTextWordGuess.text.toString()
//        if (guess.length != wordToGuess.length) {
//            textViewFeedback.text = "Tahmin edilen kelimenin uzunluğu geçersiz."
//            textViewFeedback.setTextColor(Color.RED)
//            textViewFeedback.visibility = TextView.VISIBLE
//            return
//        }
//
//        var correctCount = 0
//        var partialCount = 0
//
//        // Tahminin doğruluğunu kontrol et
//        for (i in guess.indices) {
//            val guessLetter = guess[i]
//            val actualLetter = wordToGuess[i]
//
//            if (guessLetter == actualLetter) {
//                guessedLetters[i] = true
//                correctCount++
//            } else if (wordToGuess.contains(guessLetter, ignoreCase = true)) {
//                partialCount++
//            }
//        }
//
//        // Geri bildirim oluştur
//        val feedbackMessage = StringBuilder()
//        if (correctCount == wordToGuess.length) {
//            feedbackMessage.append("Tebrikler! Kelimeyi doğru tahmin ettiniz.")
//            textViewFeedback.setTextColor(Color.GREEN)
//            buttonSubmitGuess.isEnabled = false // Oyun bittiği için tahmin butonunu devre dışı bırak
//        } else {
//            feedbackMessage.append("Doğru harf sayısı: $correctCount\n")
//            feedbackMessage.append("Yanlış yerdeki harf sayısı: $partialCount")
//            textViewFeedback.setTextColor(Color.BLACK)
//        }
//
//        textViewFeedback.text = feedbackMessage.toString()
//        textViewFeedback.visibility = TextView.VISIBLE
//        updateLetterBoxes(guess)
//        createNewRowLayout()
//    }
//
//    private fun updateLetterBoxes(guess: String) {
//        val tableLayout = findViewById<TableLayout>(R.id.tableLayoutWordBoxes)
//
//        var tableRow = TableRow(this)
//        tableRow.layoutParams = TableRow.LayoutParams(
//            TableRow.LayoutParams.MATCH_PARENT,
//            TableRow.LayoutParams.WRAP_CONTENT
//        )
//
//        for (i in guess.indices) {
//            val textView = TextView(this)
//            textView.text = guess[i].toString()
//            textView.gravity = Gravity.CENTER
//            textView.width = resources.getDimensionPixelSize(R.dimen.box_size)
//            textView.height = resources.getDimensionPixelSize(R.dimen.box_size)
//            textView.setBackgroundResource(R.drawable.letter_box)
//
//            // Doğru tahmin edilen harfleri yeşil, yanlış tahmin edilenleri sarı yap
//            if (guessedLetters[i]) {
//                textView.setTextColor(Color.GREEN)
//            } else if (wordToGuess.contains(guess[i], ignoreCase = true)) {
//                textView.setTextColor(Color.YELLOW)
//            }
//
//            tableRow.addView(textView)
//
//            // Her 5 harf için yeni bir satır oluştur
//            if ((i + 1) % 5 == 0) {
//                tableLayout.addView(tableRow)
//                tableRow = TableRow(this)
//                tableRow.layoutParams = TableRow.LayoutParams(
//                    TableRow.LayoutParams.MATCH_PARENT,
//                    TableRow.LayoutParams.WRAP_CONTENT
//                )
//            }
//        }
//
//        // Son kalan satırı ekle
//        tableLayout.addView(tableRow)
//    }
//
//
//
//
//    private fun createNewRowLayout() {
//        tableLayoutWordBoxes.addView(View(this)) // Yeni bir boş görünüm ekleyerek satır sonu oluştur
//    }
//
//}
