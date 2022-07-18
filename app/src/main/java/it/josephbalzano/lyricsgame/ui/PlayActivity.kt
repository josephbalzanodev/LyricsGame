package it.josephbalzano.lyricsgame.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
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
import it.josephbalzano.lyricsgame.ui.adapter.CardAdapter
import it.josephbalzano.lyricsgame.ui.adapter.TYPE_QUIZ
import it.josephbalzano.lyricsgame.ui.adapter.TYPE_START
import it.josephbalzano.lyricsgame.ui.model.TutorialCard
import it.josephbalzano.lyricsgame.utils.Extension.setBlueNavigationBar
import it.josephbalzano.lyricsgame.utils.Extension.takeRandom
import it.josephbalzano.lyricsgame.utils.Preferences
import it.josephbalzano.lyricsgame.viewmodel.PlayViewModel
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity(), CardStackListener,
    CardAdapter.QuizCardListener {
    val model: PlayViewModel by viewModels()

    private val thisAct = this@PlayActivity

    private var layoutManager: CardStackLayoutManager? = null
    private var adapter: CardAdapter? = null

    private var isInGame: Boolean = false
    private val keyPrefs: String = "tutorialCompleted"

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
        val listOfCards =
                if (!Preferences.get(thisAct, keyPrefs, false))
                    listOf(
                            TutorialCard(
                                    getString(R.string.play_card_tutorial_first_card_title),
                                    getString(R.string.play_card_tutorial_first_card_text),
                                    getString(R.string.play_card_tutorial_first_card_button)
                            ),
                            TutorialCard(
                                    getString(R.string.play_card_tutorial_second_card_title),
                                    getString(R.string.play_card_tutorial_second_card_text),
                                    getString(R.string.play_card_tutorial_second_card_button)
                            ),
                            TutorialCard(
                                    getString(R.string.play_card_tutorial_third_card_title),
                                    getString(R.string.play_card_tutorial_third_card_text),
                                    getString(R.string.play_card_tutorial_third_card_button)
                            ),
                            TutorialCard(
                                    getString(R.string.play_card_tutorial_four_card_title),
                                    getString(R.string.play_card_tutorial_four_card_text),
                                    getString(R.string.play_card_tutorial_four_card_button)
                            ),
                            Any()
                    )
                else listOf(Any())

        adapter = CardAdapter(
                listOfCards.toMutableList().apply {
                    ShareData.tracksMap.takeRandom(6).forEach {
                        add(it)
                    }
                }, this
        )

        name.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                ) = Unit

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
                model.saveData(name.text).observe(thisAct, Observer {
                    if (it) finish()
                })
            }
        }
    }

    private fun swipeCard() =
            model.apply {
                currentPosQuiz++
                cards.swipe()
            }

    private fun initObserver() =
            model.apply {
                getRemainTime()
                        .observe(thisAct, Observer {
                            time.text = it.toString()

                            if (it == 0) {
                                if (isInGame)
                                    thisAct.onError(this.currentPosQuiz)
                                else swipeCard()
                            }
                        })

                observeScore()
                        .observe(thisAct, Observer { score.text = it.toString() })
            }

    override fun onCardDisappeared(view: View?, position: Int) =
            position.let {
                if (adapter!!.itemCount - 1 == it) {
                    model.stopTime()
                    postGame.visibility = VISIBLE
                }
            }

    override fun onCardAppeared(view: View?, position: Int) {
        when (adapter!!.getItemViewType(position)) {
            TYPE_START -> {
                isInGame = false
                model.restartCountDown(PlayViewModel.Countdown.THIRD)
            }
            TYPE_QUIZ -> {
                isInGame = true
                Preferences.put(thisAct, keyPrefs, true)
                model.restartCountDown(PlayViewModel.Countdown.TEN)
            }
        }
    }

    override fun tutorialNext() = swipeCard()

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
    //endregion
}