package it.josephbalzano.lyricsgame.ui

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.ShareData.tracksMap
import it.josephbalzano.lyricsgame.ui.adapter.CardAdapter
import it.josephbalzano.lyricsgame.utils.NavigationBar
import it.josephbalzano.lyricsgame.viewmodel.PlayViewModel
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity(), CardStackListener,
    CardAdapter.ViewHolder.QuizCardListener {

    val model: PlayViewModel by viewModels()

    private val adapter = CardAdapter(
        quizCards = tracksMap.shuffled().take(3),
        listener = this
    )
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
        layoutManager!!.setCanScrollHorizontal(false)
        layoutManager!!.setCanScrollVertical(false)
        swipeableCard.layoutManager = layoutManager
        swipeableCard.adapter = adapter

        initObserver()
        initButtons()
        initView()
    }

    private fun initView() {
        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                save.isEnabled = !name.text.isNullOrEmpty()
            }
        })
    }

    private fun initButtons() {
        save.setOnClickListener {
            model.saveData(name.text).observe(this, Observer {
                if (it) finish()
            })
        }
    }

    private fun initObserver() {
        model.getRemainTime()
            .observe(this, Observer { time.text = it.toString() })
        model.observeScore()
            .observe(this, Observer { score.text = it.toString() })
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        if (adapter.itemCount - 1 == position) {
            model.stopTime()
            postGame.visibility = VISIBLE
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
        model.restartCountDown()
    }

    override fun onCardRewound() {
    }

    override fun onStartQuiz() {
    }

    override fun onCorrect() {
        model.addCorrectResponse()
        swipeableCard.swipe()
    }

    override fun onError() {
        swipeableCard.swipe()
    }

}