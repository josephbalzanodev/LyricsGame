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
import it.josephbalzano.lyricsgame.ui.model.TutorialCard
import it.josephbalzano.lyricsgame.utils.Extension.takeRandom
import kotlinx.android.synthetic.main.quiz_item.view.*
import kotlinx.android.synthetic.main.quiz_item.view.quizBack
import kotlinx.android.synthetic.main.quiz_tutorial.view.*

const val TYPE_TUTORIAL: Int = 0x3
const val TYPE_START: Int = 0x2
const val TYPE_QUIZ: Int = 0x1

class CardAdapter(
    var quizCards: List<Any> = listOf(),
    var listener: QuizCardListener
) : RecyclerView.Adapter<CardAdapter.BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            TYPE_QUIZ ->
                ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.quiz_item,
                            parent,
                            false
                        ),
                    listener
                )
            TYPE_TUTORIAL ->
                TutorialHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.quiz_tutorial,
                            parent,
                            false
                        ),
                    listener
                )
            else ->
                StartHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.quiz_start,
                            parent,
                            false
                        )
                )
        }


    override fun getItemViewType(position: Int): Int =
        when (quizCards[position]) {
            is QuizCard -> TYPE_QUIZ
            is TutorialCard -> TYPE_TUTORIAL
            else -> TYPE_START
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
        when (holder) {
            is ViewHolder ->
                holder.bind(quizCards[position] as QuizCard, position)
            is TutorialHolder ->
                holder.bind(quizCards[position] as TutorialCard, position)
            else -> Unit
        }

    override fun getItemCount(): Int = quizCards.size

    class ViewHolder(
        view: View,
        private var listener: QuizCardListener
    ) : BaseViewHolder(view) {
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
    }

    class TutorialHolder(
        view: View,
        private var listener: CardAdapter.QuizCardListener
    ) : BaseViewHolder(view) {
        fun bind(card: TutorialCard, pos: Int) {
            itemView.apply {
                title.text = card.title
                text.text = card.text
                next.text = card.button

                next.setOnClickListener { listener.tutorialNext() }
            }
        }
    }

    class StartHolder(view: View) : BaseViewHolder(view)

    open class BaseViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)

    interface QuizCardListener {
        fun tutorialNext()
        fun onCorrect(pos: Int)
        fun onError(pos: Int)
    }
}