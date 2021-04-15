package it.josephbalzano.lyricsgame.network.model

data class MusicGenreX(
    val music_genre_id: Int,
    val music_genre_name: String,
    val music_genre_name_extended: String,
    val music_genre_parent_id: Int,
    val music_genre_vanity: String
)