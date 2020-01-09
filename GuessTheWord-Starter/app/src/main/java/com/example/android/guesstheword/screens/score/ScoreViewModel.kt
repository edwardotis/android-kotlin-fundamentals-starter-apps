package com.example.android.guesstheword.screens.score

import androidx.lifecycle.ViewModel
import timber.log.Timber

class ScoreViewModel (finalScore:Int): ViewModel(){
//class ScoreViewModel (private val finalScore:Int): ViewModel(){
    val score = finalScore

    init{
        Timber.i("init ScoreViewModel with $finalScore")
    }
}