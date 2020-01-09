package com.example.android.guesstheword.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class GameViewModel : ViewModel() {

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


    // The list of words - the front of the list is the next word to guess
    lateinit var wordList: MutableList<String>

    init {
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()
        Timber.i("GameViewModel created!")
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        if (!wordList.isEmpty()) {
            //Select and remove a _word from the list
            _word.value = wordList.removeAt(0)
        }
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
        Timber.i("GameViewModel destroyed!")
    }
}