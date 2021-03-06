/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding
import timber.log.Timber

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Timber.i("Called ViewModelProviders.of")
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)
//        viewModel.score.observe(this, Observer { newScore ->
//            binding.scoreText.text = newScore.toString()
//        })
//        viewModel.word.observe(this, Observer { newWord ->
//            binding.wordText.text = newWord
//        })
        viewModel.eventGameFinished.observe(this, Observer { isFinished ->
            if(isFinished) gameFinished()//single line if conditional
        })
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        // Set the viewmodel for databinding - this allows the bound layout access
        // to all the data in the ViewModel
        binding.gameViewModel = viewModel

        // Specify the current activity as the lifecycle owner of the binding.
// This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun gameFinished(){
        Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
        val action = GameFragmentDirections.actionGameToScore()
        //score gets a default value in viewModel init block, so always safe.
        action.score = viewModel.score.value ?: 0//or we just handle a default here as well
        NavHostFragment.findNavController(this).navigate(action)
        // Ed WARNING: If default value is set for arg, then you have to turn a one liner into a 3 line statment
        // in order to provide args, but that data maybe belongs in ViewModel anyway? Share b/t frags or no?
//        findNavController().navigate(GameFragmentDirections.actionGameToScore(viewModel.score))
        viewModel.onGameFinishComplete()

    }


    /** Methods for buttons presses **/
    private fun onEndGame(){
        gameFinished()
    }

//    private fun onSkip() {
//        viewModel.onSkip()
////        updateWordText()
////        updateScoreText()
//    }
//
//    private fun onCorrect() {
//        viewModel.onCorrect()
////        updateWordText()
////        updateScoreText()
//    }


    /** Methods for updating the UI **/
    //NOW HANDLED BY LIVEDATA OBSERVERS

//    private fun updateWordText() {
//        binding.wordText.text = viewModel.word.value
//    }
//
//    private fun updateScoreText() {
//        binding.scoreText.text = viewModel.score.value.toString()
//    }
}
