package com.example.yazlab2proje2.Models

data class Game(
    val gameId: String,
    val player1Id: String,
    val player2Id: String,
    val gameType: String, // Oyun türü (örneğin: harf sabitli, harf sabitli olmayan)
    val wordLength: Int, // Kelime uzunluğu
    val gameState: String, // Oyun durumu (örneğin: başlamadı, devam ediyor, tamamlandı)
    val word: String,
    val player1Guesses: List<String>, // Oyuncu 1'in tahminleri
    val player2Guesses: List<String>, // Oyuncu 2'nin tahminleri
    val winnerId: String? // Oyunun kazananı
)

