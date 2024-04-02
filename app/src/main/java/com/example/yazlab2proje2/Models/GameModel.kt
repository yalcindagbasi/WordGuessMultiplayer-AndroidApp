package com.example.yazlab2proje2.Models
import java.util.*

data class GameModel(
    val gameId: String="-1",
    val player1Id: String="-1",
    var player2Id: String="-1",
    val wordLength: Int=-1, // Kelime uzunluğu
    var gameState: GameStatus = GameStatus.CREATED, // Oyun durumu (örneğin: başlamadı, devam ediyor, tamamlandı)
    val word: String="-1",
    val player1Guesses: MutableList<String> = mutableListOf(""), // Oyuncu 1'in tahminleri
    val player2Guesses: MutableList<String> = mutableListOf(""), // Oyuncu 2'nin tahminleri
    val winnerId: String="-1", // Oyunun kazananı
)
enum class GameStatus{
    CREATED,
    JOINED,
    INPROGRESS,
    FINISHED
}
