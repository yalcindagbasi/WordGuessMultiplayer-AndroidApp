package com.example.yazlab2proje2
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.firestore.FirebaseFirestore
import com.example.yazlab2proje2.Models.User
import com.example.yazlab2proje2.Models.RoomType
import com.example.yazlab2proje2.Models.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration

class RoomActivity : AppCompatActivity() {


    private val db = FirebaseFirestore.getInstance()
    private var oldRoomType: RoomType? = null
    private var oldUserState: UserState? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var inGame = false
    private var letterCount = 4
    private var roomListener: ListenerRegistration? = null // Listener'ı tutacak değişkeni tanımlayın
    private var joinGameListener: ListenerRegistration? = null // Listener'ı tutacak değişkeni tanımlayın

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        Log.d("TAG", "******RoomActivity onCreate")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        letterCount = intent.getIntExtra("lettercount",4)
        Log.d("TAG", "Gelen Letter count: $letterCount")
        val roomType = intent.getSerializableExtra("roomType") as RoomType

        Log.d("TAG", "RoomType: $roomType")
        // ANASAYFA BUTONU
        val btnHomePage = findViewById<Button>(R.id.btn_HomePage)
        btnHomePage.setOnClickListener {
            // Ana sayfaya geri dön
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        // HESAPTAN ÇIKIŞ YAP BUTONU
        val btnSignOut = findViewById<Button>(R.id.btn_Logout)
        btnSignOut.setOnClickListener {
            // Firebase oturumu kapat ve giriş ekranına yönlendir
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            if (userId != null) {
                Utils.updateUserState(userId, UserState.OFFLINE)
            }
            startActivity(intent)
            finish()
        }

        // ÇIKIŞ YAP BUTONU


        val buttonLogout = findViewById<Button>(R.id.btn_ExitGame)
        buttonLogout.setOnClickListener {
            // Uygulamadan çıkış yap
            if (userId != null) {
                Utils.updateUserState(userId, UserState.OFFLINE)
            }
            finishAffinity()

        }

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    oldRoomType = user?.roomType
                    oldUserState = user?.state

                    db.collection("users")
                        .document(userId)
                        .update(mapOf("roomType" to roomType, "state" to UserState.INROOM))
                        .addOnSuccessListener {
                            // Güncelleme başarılı
                        }
                        .addOnFailureListener { e ->
                            // Güncelleme başarısız
                        }
                }
        }


        getUsersInRoom(roomType)

    }

    override fun onResume() {
        super.onResume()

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Kullanıcının roomType ve UserState'ini eski haline getir

        if (userId != null && oldRoomType != null && oldUserState != null) {
            db.collection("users")
                .document(userId)
                .update(mapOf("roomType" to oldRoomType, "state" to oldUserState))
                .addOnSuccessListener {
                    // Güncelleme başarılı
                }
                .addOnFailureListener { e ->
                    // Güncelleme başarısız
                }
        }

    }

    private fun getUsersInRoom(roomType: RoomType) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        roomListener = db.collection("users")
            .whereEqualTo("roomType", roomType)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val scrollView = findViewById<ScrollView>(R.id.scrollview)
                val linearLayout = scrollView.getChildAt(0) as LinearLayout
                linearLayout.removeAllViews() // Önceki görünümler temizle

                for (document in snapshot!!.documents) {
                    val user = document.toObject(User::class.java)
                    if(user?.userId != "" && user?.userId!=currentUserId){
                        // Her bir kullanıcı için bir CardView oluştur
                        val cardView = CardView(this)

                        // CardView özelliklerini ayarla
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(16, 16, 16, 16)
                        cardView.layoutParams = params
                        cardView.radius = 15F
                        cardView.setContentPadding(25, 25, 25, 25)
                        cardView.setCardBackgroundColor(Color.White.toArgb())
                        cardView.maxCardElevation = 15F
                        cardView.cardElevation = 9F

                        // CardView içinde bir LinearLayout oluştur
                        val innerLayout = LinearLayout(this)
                        innerLayout.orientation = LinearLayout.VERTICAL

                        // Kullanıcı adını gösteren TextView oluştur
                        val usernameTextView = TextView(this)
                        usernameTextView.layoutParams = params
                        usernameTextView.text = user?.username
                        usernameTextView.textSize = 30F

                        // Kullanıcı durumunu gösteren TextView oluştur
                        val userStateTextView = TextView(this)
                        userStateTextView.layoutParams = params
                        userStateTextView.text = user?.state.toString()
                        userStateTextView.textSize = 20F

                        // Davet butonunu oluştur
                        val inviteButton = Button(this)
                        inviteButton.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        // Butona bir tıklama dinleyicisi ekle
                        inviteButton.setOnClickListener {
                            // Butona tıklandığında yapılacak işlemler
                            if (inviteButton.text == "Davet Et") {
                                Log.d("RoomActivity", "${user?.username} adlı kullanıcıya davet gönderildi.")
                                sendInvite(currentUserId!!, user!!.userId)
                            } else {
                                Log.d("RoomActivity", "${user?.username} adlı kullanıcının daveti kabul edildi.")
                                inviteButton.isEnabled=false
                                acceptInvite(user!!.userId, currentUserId!!)
                            }
                        }

                        // TextView'ları ve butonu LinearLayout'a ekle
                        innerLayout.addView(usernameTextView)
                        innerLayout.addView(userStateTextView)
                        innerLayout.addView(inviteButton)

                        // LinearLayout'ı CardView'a ekle
                        cardView.addView(innerLayout)

                        // CardView'ı ana LinearLayout'a ekle
                        linearLayout.addView(cardView)

                        // Kullanıcının aldığı ve gönderdiği davetleri kontrol et
                        if (user?.receivedInvites?.contains(currentUserId!!) == true) {
                            inviteButton.text = "Davet Gönderildi"
                            inviteButton.isEnabled = false
                        } else if (user?.sentInvites?.contains(currentUserId!!) == true) {
                            inviteButton.text = "Daveti Kabul Et"

                        } else {
                            inviteButton.text = "Davet Et"
                        }
                    }

                }
            }
    }
    // A kişisi B kişisine davet gönderdiğinde bu metodu çağır
    fun sendInvite(fromUserId: String, toUserId: String) {
        // A'nın sentInvites listesine B'yi ekle

        db.collection("users")
            .document(fromUserId)
            .update("sentInvites", FieldValue.arrayUnion(toUserId))
            .addOnSuccessListener {
                // B'nin receivedInvites listesine A'yı ekle
                db.collection("users")
                    .document(toUserId)
                    .update("receivedInvites", FieldValue.arrayUnion(fromUserId))
                    .addOnSuccessListener {
                        // Davet başarıyla gönderildi
                    }
                    .addOnFailureListener { e ->
                        // Davet gönderilirken bir hata oluştu
                    }
            }
            .addOnFailureListener { e ->
                // Davet gönderilirken bir hata oluştu
            }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && !inGame) {
            inGame=true;
            joinGameListener = FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        if (user != null && user.state == UserState.INGAME) {
                            // Kullanıcı zaten bir oyunda, bu yüzden JoinGameActivity'ye yönlendir
                            val intent = Intent(this, JoinGameActivity::class.java)
                            intent.putExtra("GAME_ID", user.gameId)
                            Log.d("RoomActivity", "*******RoomActivity 138 Joining game with ID: ${user.gameId}")
                            intent.putExtra("player2Id", userId)

                            finish()
                            startActivity(intent)

                        }
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }
        }
    }





    // B kişisi "Davet gönderildi" butonuna bastığında bu metodu çağır
    fun acceptInvite(fromUserId: String, toUserId: String) {
        // B'nin receivedInvites listesinden A'yı kaldır
        db.collection("users")
            .document(toUserId)
            .update("receivedInvites", FieldValue.arrayRemove(fromUserId))
            .addOnSuccessListener {
                // A'nın sentInvites listesinden B'yi kaldır
                db.collection("users")
                    .document(fromUserId)
                    .update("sentInvites", FieldValue.arrayRemove(toUserId))
                    .addOnSuccessListener {
                        // Davet başarıyla kabul edildi
                        // A ve B'yi yeni bir ekrana yönlendir

                        val intent = Intent(this, CreateGameActivity::class.java)
                        Log.d("RoomActivity", "********RoomActivity 304 Starting game with $fromUserId and $toUserId")
                        intent.putExtra("player2Id", fromUserId)
                        intent.putExtra("lettercount",letterCount.toString())
                        finish()
                        startActivity(intent)

                    }
                    .addOnFailureListener { e ->
                        // Davet kabul edilirken bir hata oluştu
                    }
            }
            .addOnFailureListener { e ->
                // Davet kabul edilirken bir hata oluştu
            }
    }
    override fun onBackPressed() {
        // Geri butonuna basıldığında yapılacak işlemler
        roomListener?.remove()
        // Örneğin, kullanıcının durumunu 'OFFLINE' olarak güncelleyebiliriz
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Utils.updateUserState(userId, UserState.ONLINE)
        }

        // Son olarak, super.onBackPressed() çağrısını yaparak geri butonunun varsayılan davranışını gerçekleştiririz
        super.onBackPressed()
    }
    override fun onDestroy() {
        roomListener?.remove() // Listener'ı kaldır
        joinGameListener?.remove() // Listener'ı kaldır
        super.onDestroy()
    }



}