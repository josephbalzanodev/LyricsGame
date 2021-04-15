package it.josephbalzano.lyricsgame.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.model.QuizCard
import kotlinx.android.synthetic.main.quiz_item.view.*
import kotlin.random.Random

class CardAdapter(
    var quizCards: List<QuizCard> = listOf(),
    var listener: ViewHolder.QuizCardListener
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.quiz_item, parent, false),
            listener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(quizCards[position])

    override fun getItemCount(): Int = quizCards.size

    class ViewHolder(
        view: View,
        var listener: QuizCardListener
    ) : RecyclerView.ViewHolder(view) {
        var listButtons = listOf(itemView.first, itemView.second, itemView.third)

        fun bind(card: QuizCard) {
            itemView.quizPhrase.text = card.lyrics[Random.nextInt(0, card.lyrics.size)]

            card.possibilityArtist
                .shuffled()
                .forEachIndexed { index, s ->
                    listButtons[index].text = s
                }

            listButtons.forEach {
                it.setOnClickListener { v ->
                    if ((v as Button).text == card.artist)
                        listener.onCorrect()
                    else listener.onError()
                }
            }
        }

        interface QuizCardListener {
            fun onStartQuiz()
            fun onCorrect()
            fun onError()
        }
    }
}