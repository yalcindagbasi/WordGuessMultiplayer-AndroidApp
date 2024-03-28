package com.example.yazlab2proje2

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private lateinit var kelime: String
    private lateinit var tableLayout: TableLayout
    private lateinit var guessInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        kelime = "masa" // Tahmin edilmesi gereken kelime
        tableLayout = findViewById(R.id.tableLayout)
        guessInput = findViewById(R.id.guessInput)
        val guessButton: Button = findViewById(R.id.guessButton)
        guessButton.setOnClickListener { tahminEt() }

        kelimeyiGoster()
    }

    private fun kelimeyiGoster() {
        val row = TableRow(this)
        row.gravity = Gravity.CENTER

        for (harf in kelime) {
            val textView = TextView(this)
            textView.text = "_"
            textView.setTextColor(Color.BLACK)
            textView.textSize = 24f
            textView.setPadding(8, 0, 8, 0)
            row.addView(textView)
        }

        tableLayout.addView(row)
    }

    private fun tahminEt() {
        val tahmin = guessInput.text.toString()

        val row = TableRow(this)
        row.gravity = Gravity.CENTER

        for (i in tahmin.indices) {
            val textView = TextView(this)
            textView.text = tahmin[i].toString()
            val harf = kelime[i]
            if (tahmin[i] == harf) {
                textView.setTextColor(Color.GREEN)
            } else if (kelime.contains(tahmin[i])) {
                textView.setTextColor(Color.YELLOW)
            } else {
                textView.setTextColor(Color.GRAY)
            }
            textView.textSize = 24f
            textView.setPadding(8, 0, 8, 0)
            row.addView(textView)
        }

        tableLayout.addView(row)

        if (tahmin.equals(kelime, ignoreCase = true)) {
            // Oyunu bitir, kazandınız mesajı göster
        }
        guessInput.text.clear()
    }
}