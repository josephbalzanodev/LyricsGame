package it.josephbalzano.lyricsgame.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.model.QuizCard
import it.josephbalzano.lyricsgame.utils.Extension.takeRandom
import kotlinx.android.synthetic.main.quiz_item.view.*

class CardAdapter(
    var quizCards: List<QuizCard> = listOf(),
    var listener: ViewHolder.QuizCardListener
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.quiz_item,
                    parent,
                    false
                ),
            listener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(quizCards[position], position)

    override fun getItemCount(): Int = quizCards.size

    class ViewHolder(
        view: View,
        private var listener: QuizCardListener
    ) : RecyclerView.ViewHolder(view) {
        private var listButtons = listOf(itemView.first, itemView.second, itemView.third)

        fun bind(card: QuizCard, pos: Int) {
            itemView.apply {
                buttonsIsEnabled(true)
                quizBack.backgroundTintList = ColorStateList.valueOf(Color.WHITE)

                quizPhrase.text =
                    card.lyrics
                        .takeRandom(1)[0]
            }

            card.possibilityArtist
                .shuffled()
                .forEachIndexed { index, s ->
                    listButtons[index].text = s
                }

            listButtons.forEach {
                it.setOnClickListener { v ->
                    if ((v as Button).text == card.artist)
                        listener.onCorrect(pos)
                    else listener.onError(pos)

                    buttonsIsEnabled(false)
                }
            }
        }

        private fun buttonsIsEnabled(enabled: Boolean) =
            listButtons.forEach { it.isEnabled = enabled }

        interface QuizCardListener {
            fun onStartQuiz()
            fun onCorrect(pos: Int)
            fun onError(pos: Int)
        }
    }
}