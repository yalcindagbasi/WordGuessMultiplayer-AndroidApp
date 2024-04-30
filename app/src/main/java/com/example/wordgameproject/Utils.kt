package com.example.wordgameproject

import android.util.Log
import com.example.wordgameproject.Models.UserState
import com.google.firebase.firestore.FirebaseFirestore

object Utils {
    fun updateUserState(userId: String, newState: UserState) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(userId)
            .update("state", newState)
            .addOnSuccessListener {
                Log.d("MainActivity", "User state successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error updating user state", e)
            }
    }

}