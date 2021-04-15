package it.josephbalzano.lyricsgame.ui.model

data class QuizCard(
    val artist: String,
    val possibilityArtist: List<String>,
    val lyrics: List<String>
)
