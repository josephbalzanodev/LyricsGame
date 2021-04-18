package it.josephbalzano.lyricsgame.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Observer
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.ShareData.tracksMap
import it.josephbalzano.lyricsgame.ui.adapter.CardAdapter
import it.josephbalzano.lyricsgame.utils.Extension.setBlueNavigationBar
import it.josephbalzano.lyricsgame.utils.Extension.takeRandom
import it.josephbalzano.lyricsgame.viewmodel.PlayViewModel
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity(), CardStackListener,
    CardAdapter.ViewHolder.QuizCardListener {

    val model: PlayViewModel by viewModels()

    private val thisAct = this@PlayActivity

    private var layoutManager: CardStackLayoutManager? = null
    private val adapter = CardAdapter(
        quizCards = tracksMap.takeRandom(6),
        listener = this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        setBlueNavigationBar()

        layoutManager =
            CardStackLayoutManager(baseContext, this)
                .apply {
                    setStackFrom(StackFrom.Top)
                    setCanScrollHorizontal(false)
                    setCanScrollVertical(false)
                }

        initObserver()
        initView()
    }

    private fun initView() {
        name.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) =
                    Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                    save.let { it.isEnabled = !name.text.isNullOrEmpty() }
            })
        }

        cards.apply {
            layoutManager = thisAct.layoutManager
            adapter = thisAct.adapter
        }

        save.apply {
            setOnClickListener {
                model.saveData(name.text)
                    .observe(thisAct, Observer {
                        if (it) finish()
                    })
            }
        }
    }

    private fun initObserver() =
        model.apply {
            getRemainTime()
                .observe(thisAct, Observer {
                    time.text = it.toString()

                    if (it == 0) thisAct.onError(this.currentPosQuiz)
                })

            observeScore()
                .observe(thisAct, Observer { score.text = it.toString() })
        }

    override fun onCardDisappeared(view: View?, position: Int) =
        position.let {
            if (adapter.itemCount - 1 == it) {
                model.stopTime()
                postGame.visibility = VISIBLE
            }
        }

    override fun onCardAppeared(view: View?, position: Int) {
        model.restartCountDown()
    }

    override fun onCorrect(pos: Int) {
        model.apply {
            addCorrectResponse()
            stopTime()
            currentPosQuiz++
        }

        colorAnimation(
            pos,
            resources.getColor(R.color.quizBackStartColor),
            resources.getColor(R.color.quizBackCorrectColor)
        )
    }

    override fun onError(pos: Int) {
        model.apply {
            stopTime()
            currentPosQuiz++
        }

        getViewFromQuizCard<View>(pos, R.id.quizBack)?.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.wiggie_anim
            )
        )

        colorAnimation(
            pos,
            resources.getColor(R.color.quizBackStartColor),
            resources.getColor(R.color.quizBackErrorColor)
        )
    }

    private fun <T : View?> getViewFromQuizCard(pos: Int, id: Int): T? =
        cards.layoutManager?.findViewByPosition(pos)?.findViewById<T>(id)

    private fun colorAnimation(pos: Int, colorStart: Int, colorEnd: Int) =
        ValueAnimator().setDuration(1500L)
            .apply {
                setIntValues(colorStart, colorEnd)
                setEvaluator(ArgbEvaluator())
                addUpdateListener { animator ->
                    getViewFromQuizCard<View>(pos, R.id.quizBack)
                        ?.backgroundTintList =
                        ColorStateList.valueOf(animator.animatedValue as Int)
                }
                doOnEnd { cards.swipe() }
                start()
            }

    // region Unused listener functions
    override fun onCardDragging(direction: Direction?, ratio: Float) = Unit

    override fun onCardSwiped(direction: Direction?) = Unit

    override fun onCardCanceled() = Unit

    override fun onCardRewound() = Unit

    override fun onStartQuiz() = Unit
    //endregion
}