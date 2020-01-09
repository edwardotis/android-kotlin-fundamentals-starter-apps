package com.example.android.guesstheword.screens.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class ScoreViewModel (finalScore:Int): ViewModel(){
//class ScoreViewModel (private val finalScore:Int): ViewModel(){
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    init{
        Timber.i("init ScoreViewModel with $finalScore")
        _score.value = finalScore
    }
}