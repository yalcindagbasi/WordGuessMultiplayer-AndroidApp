package com.example.yazlab2proje2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yazlab2proje2.Models.GameModel
import com.example.yazlab2proje2.Models.GameStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object GameData {
    private var _gameModel : MutableLiveData<GameModel> = MutableLiveData()
    var gameModel : LiveData<GameModel> = _gameModel

    fun saveGameModel(model : GameModel){
        _gameModel.postValue(model)
        Firebase.firestore.collection("games")
            .document(model.gameId)
            .set(model)
    }

    fun fetchGameModel(gameId : String) {
        gameModel.value?.apply {
            Firebase.firestore.collection("games")
                .document(gameId)
                .addSnapshotListener { value, error ->
                    val model = value?.toObject(GameModel::class.java)
                    _gameModel.postValue(model)

                }
        }
    }

}