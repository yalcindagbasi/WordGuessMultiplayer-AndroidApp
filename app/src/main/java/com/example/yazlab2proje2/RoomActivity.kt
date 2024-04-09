package com.example.yazlab2proje2
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
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

class RoomActivity : AppCompatActivity() {


    private val db = FirebaseFirestore.getInstance()
    private var oldRoomType: RoomType? = null
    private var oldUserState: UserState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
        val roomType = intent.getSerializableExtra("roomType") as RoomType
        val gameType = intent.getStringExtra("gameType")
        val wordLength = intent.getIntExtra("wordLength",4)
        Log.d("TAG", "RoomType: $roomType")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
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
override fun onPause() {
    super.onPause()

    // Kullanıcının roomType ve UserState'ini güncelle
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {

        db.collection("users")
            .document(userId)
            .update(mapOf("roomType" to RoomType.NOT))
            .addOnSuccessListener {
                // Güncelleme başarılı
            }
            .addOnFailureListener { e ->
                // Güncelleme başarısız
            }
    }
}

    override fun onResume() {
        super.onResume()

        // Kullanıcının roomType ve UserState'ini eski haline getir
        val userId = FirebaseAuth.getInstance().currentUser?.uid
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
        db.collection("users")
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
                    inviteButton.text = "Davet"
                    inviteButton.layoutParams = params

                    // Butona bir tıklama dinleyicisi ekle
                    inviteButton.setOnClickListener {
                        // Butona tıklandığında yapılacak işlemler
                        Log.d("RoomActivity", "${user?.username} adlı kullanıcıya davet gönderildi.")
                        sendInvite(FirebaseAuth.getInstance().currentUser?.uid!!, user!!.userId)
                    }

                    // TextView'ları ve butonu LinearLayout'a ekle
                    innerLayout.addView(usernameTextView)
                    innerLayout.addView(userStateTextView)
                    innerLayout.addView(inviteButton)

                    // LinearLayout'ı CardView'a ekle
                    cardView.addView(innerLayout)

                    // CardView'ı ana LinearLayout'a ekle
                    linearLayout.addView(cardView)

                    // Kullanıcının aldığı davetleri kontrol et
                    checkInvites(user!!.userId)
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
    }


    // B kişisi uygulamayı açtığında bu metodu çağır
    fun checkInvites(userId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseFirestore.getInstance().collection("users")
            .document(userId!!)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    if (user.state == UserState.INGAME) {
                        // Kullanıcı zaten bir oyunda, bu yüzden JoinGameActivity'ye yönlendir
                        val intent = Intent(this, JoinGameActivity::class.java)
                        intent.putExtra("GAME_ID", user.gameId)
                        intent.putExtra("player2Id", userId)
                        startActivity(intent)
                        finish()
                    } else {
                        // Kullanıcı bir oyunda değil, davetleri kontrol et
                        db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                val user = document.toObject(User::class.java)
                                val receivedInvites = user?.receivedInvites

                                // B'nin aldığı her davet için bir buton oluştur
                                receivedInvites?.forEach { invite ->
                                    // Buton oluşturma ve daveti kabul etme işlemleri
                                    val acceptButton = Button(this)
                                    acceptButton.text = "Davet Kabul Et"
                                    acceptButton.layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )

                                    // Butona bir tıklama dinleyicisi ekle
                                    acceptButton.setOnClickListener {
                                        // Butona tıklandığında yapılacak işlemler
                                        Log.d("RoomActivity", "${user?.username} adlı kullanıcının daveti kabul edildi.")
                                        acceptInvite(invite, userId)
                                    }

                                    // Butonu ana LinearLayout'a ekle
                                    val scrollView = findViewById<ScrollView>(R.id.scrollview)
                                    val linearLayout = scrollView.getChildAt(0) as LinearLayout
                                    linearLayout.addView(acceptButton)
                                }
                            }
                            .addOnFailureListener { e ->
                                // Davetler kontrol edilirken bir hata oluştu
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                // Kullanıcı bilgileri alınırken bir hata oluştu
                Log.w(TAG, "Error getting documents: ", e)
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
                        intent.putExtra("player2Id", fromUserId)
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
}