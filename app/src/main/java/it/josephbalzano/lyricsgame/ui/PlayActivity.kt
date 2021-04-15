package it.josephbalzano.lyricsgame.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.ShareData.tracksMap
import it.josephbalzano.lyricsgame.ui.adapter.CardAdapter
import it.josephbalzano.lyricsgame.ui.model.QuizCard
import it.josephbalzano.lyricsgame.utils.NavigationBar
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity(), CardStackListener,
    CardAdapter.ViewHolder.SwipeNotificationListener {

    private val adapter = CardAdapter(listener = this)
    private var layoutManager: CardStackLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NavigationBar.changeColor(
                this,
                NavigationBar.NavBarIconColor.LIGHT,
                getColor(R.color.colorPrimary)
            )

        layoutManager = CardStackLayoutManager(baseContext, this)
        layoutManager!!.setStackFrom(StackFrom.Top)

        swipeableCard.layoutManager = layoutManager
        adapter.setQuizCard(tracksMap)
        swipeableCard.adapter = adapter
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardRewound() {
    }

    override fun onRead(notification: QuizCard) {

    }
}