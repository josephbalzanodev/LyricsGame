package it.josephbalzano.lyricsgame.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.model.ChartItem
import kotlinx.android.synthetic.main.chart_item.view.*

class ChartAdapter(
    var score: List<ChartItem> = mutableListOf(),
    var listener: ViewHolder.ChartItemListener
) : RecyclerView.Adapter<ChartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chart_item, parent, false),
            listener
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(score[position], position)

    override fun getItemCount(): Int = score.size

    class ViewHolder(
        view: View,
        var listener: ChartItemListener
    ) : RecyclerView.ViewHolder(view) {
        fun bind(card: ChartItem, index: Int) {
            itemView.score.text = card.score.toString()
            itemView.scoreName.text =
                when (index) {
                    0 -> "\uD83E\uDD47 " + card.name
                    1 -> "\uD83E\uDD48 " + card.name
                    2 -> "\uD83E\uDD49 " + card.name
                    else -> card.name
                }

            itemView.quizBack.setOnClickListener {
                listener.onClick(card.name)
            }
        }

        interface ChartItemListener {
            fun onClick(username: String)
        }
    }
}