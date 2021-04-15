package it.josephbalzano.lyricsgame.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.model.ChartItem
import kotlinx.android.synthetic.main.chart_item.view.*

class ChartAdapter(
    var score: List<ChartItem> = mutableListOf()
) : RecyclerView.Adapter<ChartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chart_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(score[position])

    override fun getItemCount(): Int = score.size

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        fun bind(card: ChartItem) {
            itemView.score.text = card.score.toString()
            itemView.scoreName.text = card.name
        }
    }
}