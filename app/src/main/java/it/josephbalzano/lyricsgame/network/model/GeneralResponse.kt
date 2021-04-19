package it.josephbalzano.lyricsgame.network.model

data class GeneralResponse(
    val message: GeneralResponseInt
)

data class GeneralResponseInt(
    val header: Header
)