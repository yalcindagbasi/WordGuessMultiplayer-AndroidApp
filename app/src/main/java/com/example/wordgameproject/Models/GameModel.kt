package com.example.wordgameproject.Models

data class GameModel(
    val gameId: String = "-1",
    val player1Id: String = "-1",
    var player2Id: String = "-1",
    val wordLength: Int = -1,
    var gameState: GameStatus = GameStatus.CREATED,
    val player1word: String = "",
    val player2word: String = "",
    val player1Guesses: MutableList<String> = mutableListOf(),
    val player2Guesses: MutableList<String> = mutableListOf(),
    var winnerId: String = "-1", // Oyunun kazananı
    val timeLimit: Int = -1,
    val gameType : RoomType = RoomType.type0,
    val player1score : Int= 0,
    val player2score : Int= 0
)
enum class GameStatus{
    CREATED,
    JOINED,
    INPROGRESS,
    FINISHED
}

