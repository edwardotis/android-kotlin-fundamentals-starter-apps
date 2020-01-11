package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import timber.log.Timber

class GameViewModel : ViewModel() {

    //event which triggers end of the game
    private val _eventGameFinished = MutableLiveData<Boolean>()
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        //kotlin backing property
        get() = _word

    // The current score
    //keep mutable score private
    //basically LiveData version of getter/setter access modifiers
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        //kotlin backing property
        get() = _score

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long>
        //kotlin backing property
        get() = _timeLeft

    //INSTEAD OF THIS
//    private val _timeLeftStr = MutableLiveData<String>()
//    val timeLeftStr: LiveData<String>
//        //kotlin backing property
//        get() = _timeLeftStr

    //WE USE Transformations.map to convert one LiveData object to another.
    val timeLeftStr = Transformations.map(timeLeft){ time ->
        DateUtils.formatElapsedTime(time)
    }

    private val timer: CountDownTimer


    // The list of words - the front of the list is the next word to guess
    lateinit var wordList: MutableList<String>

    init {
        _word.value = ""
        _score.value = 0
        _eventGameFinished.value = false
        resetList()
        nextWord()
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
            override fun onFinish() {
                onGameFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = (millisUntilFinished /ONE_SECOND)
                Timber.i(_timeLeft.value.toString())
            }

        }
        timer.start()
        Timber.i("GameViewModel created!")
    }

    companion object {

        // Time when the game is over
        private const val DONE = 0L

        // Countdown time interval
        private const val ONE_SECOND = 1000L

        // Total time for the game
        private const val COUNTDOWN_TIME = 60000L

    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if (!wordList.isEmpty()) {
            //Select and remove a _word from the list
            _word.value = wordList.removeAt(0)
        }else{
            resetList()
        }
    }

    fun onGameFinish(){
        //game over man
        _eventGameFinished.value = true

    }

    fun onGameFinishComplete(){
        _eventGameFinished.value = false
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        if (!wordList.isEmpty()) {
            _score.value = _score.value?.minus(1)
        }
        nextWord()
    }

    fun onCorrect() {
        if (!wordList.isEmpty()) {
            _score.value = score.value?.plus(1)
        }
        nextWord()
    }


    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Timber.i("GameViewModel destroyed!")
    }
}