package it.josephbalzano.lyricsgame.network.model

data class TrackX(
    val album_id: Int,
    val album_name: String,
    val artist_id: Int,
    val artist_name: String,
    val commontrack_id: Int,
    val explicit: Int,
    val has_lyrics: Int,
    val has_richsync: Int,
    val has_subtitles: Int,
    val instrumental: Int,
    val num_favourite: Int,
    val primary_genres: PrimaryGenres,
    val restricted: Int,
    val track_edit_url: String,
    val track_id: Int,
    val track_name: String,
    val track_name_translation_list: List<Any>,
    val track_rating: Int,
    val track_share_url: String,
    val updated_time: String
)