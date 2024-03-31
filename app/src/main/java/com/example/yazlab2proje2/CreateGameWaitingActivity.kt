package com.example.yazlab2proje2


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CreateGameWaitingActivity : AppCompatActivity() {

    private val WAITING_TIME_MILLIS = 30000 // Bekleme süresi 30 saniye olarak ayarlandı

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_game_waiting)

        // Bekleme süresi dolana kadar bir zamanlayıcı kullanarak bekleyin
        Handler().postDelayed({
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }, WAITING_TIME_MILLIS.toLong())

        // "Beklemeyi İptal Et" butonunu tanımlayın ve tıklama olayını ayarlayın
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            cancelWaiting()
        }
    }

    private fun cancelWaiting() {
        // Bekleme süresi dolmadan önce beklemeyi iptal et
        finish()
    }
}
