
package com.example.yazlab2proje2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yazlab2proje2.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }  else {
                        // Giriş başarısız ise ve bağlantı hatası varsa özel bir hata mesajı göster
                        if (it.exception == null) {
                            Toast.makeText(this, "Bağlantı hatası. İnternet bağlantınızı kontrol edin.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Bağlantı hatası yoksa diğer hata kodlarına göre hata mesajını ayarla
                            val errorMessage = when (it.exception?.message) {
                                "The email address is badly formatted." -> "Geçersiz e-posta adresi formatı."
                                "The password is invalid or the user does not have a password." -> "Geçersiz şifre veya kullanıcı şifresi yok."
                                "There is no user record corresponding to this identifier. The user may have been deleted." -> "Bu kimliğe karşılık gelen bir kullanıcı kaydı yok."
                                else -> "Giriş yapılamadı. Lütfen tekrar deneyin."
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}