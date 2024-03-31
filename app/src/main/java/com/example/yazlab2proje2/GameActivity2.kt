package com.example.yazlab2proje2

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private lateinit var kelime: String
    private lateinit var gameLayout: LinearLayout
    private lateinit var guessInput: EditText
    private lateinit var backButton: Button // Anasayfaya dönmek için buton

    private var kutuSayisi: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        kelime = "opera" // Tahmin edilmesi gereken kelime
        gameLayout = findViewById(R.id.gameLayout)
        guessInput = findViewById(R.id.guessInput)
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { goToMain() }
        val guessButton: Button = findViewById(R.id.guessButton)
        guessButton.setOnClickListener { tahminEt() }

        kutuSayisi = if (kelime.length > 9) 9 else if (kelime.length < 3) 3 else kelime.length

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
            Toast.makeText(this, "Tebrikler! Oyunu kazandınız!", Toast.LENGTH_SHORT).show()
            guessInput.isEnabled = false // EditText'i devre dışı bırak
            val guessButton: Button = findViewById(R.id.guessButton)
            guessButton.isEnabled = false // Button'u devre dışı bırak
            backButton.visibility= View.VISIBLE
        }
        guessInput.text.clear()
    }
    fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // GameActivity'yi kapat
    }
}
