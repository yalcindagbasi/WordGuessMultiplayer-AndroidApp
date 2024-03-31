package com.example.yazlab2proje2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CreateGameActivity : AppCompatActivity() {

    private lateinit var gameIDEditText: EditText
    private lateinit var letterCountEditText: EditText
    private lateinit var timeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creategame)
        letterCountEditText = findViewById(R.id.txtfield_LetterCount)
        timeEditText = findViewById(R.id.txtfield_Time)

        val btnCreateGame = findViewById<Button>(R.id.btn_CreateGame)



        btnCreateGame.setOnClickListener{
            createGame()
        }
        //ANASAYFA BUTONU
        val btnHomePage = findViewById<Button>(R.id.btn_HomePage)
        btnHomePage.setOnClickListener {
            createGame()
            finish()
        }

        //HESAPTAN ÇIKIŞ YAP BUTONU
        val btnSignOut = findViewById<Button>(R.id.btn_Logout)
        btnSignOut.setOnClickListener {
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
        // EditText'lardan girilen verileri al

        val letterCount = letterCountEditText.text.toString()
        val time = timeEditText.text.toString()

        // Girilen verileri kontrol et
        if (letterCount.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        // GameActivity'ye geçiş yap ve girilen bilgileri iletebilirsin
        val intent = Intent(this, CreateGameWaitingActivity::class.java)

        intent.putExtra("LETTER_COUNT", letterCount)
        intent.putExtra("TIME", time)
        startActivity(intent)
    }
}
