//package com.example.yazlab2proje2
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//
//class LoginActivityeski : AppCompatActivity() {
//    var correctUsername = "1"
//    var correctPassword = "1"
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_logineski)
//
//        val editTextUsername = findViewById<EditText>(R.id.txtfield_Username)
//        val editTextPassword = findViewById<EditText>(R.id.txtfield_Pass)
//        //GİRİŞ BUTONU
//        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
//        buttonLogin.setOnClickListener {
//            val username = editTextUsername.text.toString()
//            val password = editTextPassword.text.toString()
//            runMainMenuActivity(username);
//            // Burada kullanıcı adı ve şifre doğrulaması yapabilirsiniz.
//            // Örnek olarak, burada sadece bir Toast mesajı gösterilecek.
//
//            /*if (!username.isNotEmpty() || !password.isNotEmpty()) {
//                Toast.makeText(this, "Kullanıcı adı veya şifre boş olamaz", Toast.LENGTH_SHORT).show()
//            } else if (loginAccount(username,password)) {
//                Toast.makeText(this, "Giriş Başarılı: $username", Toast.LENGTH_SHORT).show()
//
//                // Giriş başarılı olduğunda ana ekrana geçmek için Intent oluştur
//                runMainMenuActivity(username);
//            }
//            else{
//                Toast.makeText(this,"Giriş başarısız.",Toast.LENGTH_SHORT).show()
//            }*/
//        }
//
//        //KAYIT TUŞU
//        val buttonRegister= findViewById<Button>(R.id.buttonRegister)
//        buttonRegister.setOnClickListener{
//            val username = editTextUsername.text.toString()
//            val password = editTextPassword.text.toString()
//            if (!username.isNotEmpty() || !password.isNotEmpty()) {
//                Toast.makeText(this, "Kullanıcı adı veya şifre boş olamaz", Toast.LENGTH_SHORT)
//                    .show()
//            }else if(createAccount(username,password)){
//                Toast.makeText(this, "Kayıt Başarılı: $username", Toast.LENGTH_SHORT).show()
//            }else{
//                Toast.makeText(this,"Kayıt başarısız.",Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }
//
//
//    fun createAccount(username : String, password:String):Boolean{
//        // Hesap açma işlemleri
//        if(username!=correctUsername){
//            correctUsername=username
//            correctPassword=password
//            return true
//        }
//       return false
//    }
//
//
//    fun loginAccount(username : String, password:String): Boolean {
//        // Giriş Yapma işlemleri
//
//        return if(username==correctUsername && password==correctPassword){
//            true;} else{
//            false;
//        }
//    }
//    fun runMainMenuActivity(username: String){
//        val intent = Intent(this, MainActivity::class.java)
//        intent.putExtra("USERNAME", username) // Kullanıcı adını MainActivity'e aktar
//        startActivity(intent)
//        finish() // com.example.yazlab2proje2.LoginActivity'yi kapat
//    }
//}
