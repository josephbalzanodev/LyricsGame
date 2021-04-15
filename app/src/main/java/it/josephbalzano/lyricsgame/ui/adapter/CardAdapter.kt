package it.josephbalzano.lyricsgame.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.model.QuizCard
import kotlinx.android.synthetic.main.quiz_item.view.*
import kotlin.random.Random

class CardAdapter(
    var quizCards: MutableList<QuizCard> = mutableListOf(),
    var listener: ViewHolder.SwipeNotificationListener
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.quiz_item, parent, false),
            listener
        )

    fun setQuizCard(cards: MutableList<QuizCard>) {
        quizCards = cards
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(quizCards[position])

    override fun getItemCount(): Int = quizCards.size

    class ViewHolder(
        view: View,
        var listener: SwipeNotificationListener
    ) : RecyclerView.ViewHolder(view) {
        fun bind(card: QuizCard) {
            itemView.quizPhrase.text = card.lyrics[Random.nextInt(0, card.lyrics.size)]
            itemView.first.text = card.artist
            itemView.second.text = card.artist + " NO "
            itemView.third.text = card.artist + " SI"
        }

        interface SwipeNotificationListener {
            fun onRead(notification: QuizCard)
        }
    }
}