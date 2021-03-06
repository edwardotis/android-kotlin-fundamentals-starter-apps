/*
 * Copyright 2019, The Android Open Source Project
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

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    private lateinit var viewModel: SleepTrackerViewModel
    private lateinit var viewModelFactory: SleepTrackerViewModelFactory

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)
        //put onclick listener into xml w/ data binding to viewModel


        Timber.i("Called viewmodelfactory")
        val application = requireNotNull(this.activity).application
        val sleepDao = SleepDatabase.getInstance(application).sleepDao
//        //uh, without dagger, where does dao and
        viewModelFactory = SleepTrackerViewModelFactory(sleepDao, application)
        viewModel = viewModelFactory.create(SleepTrackerViewModel::class.java)

        viewModel.eventTrackingFinished.observe(this, Observer { isFinished ->
            if (isFinished) trackingFinished()
        })

        viewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) {
                Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        getString(R.string.cleared_message),
                        Snackbar.LENGTH_SHORT
                ).show()
                viewModel.doneShowingSnackbar()
            }
        })
//
//        // Set the viewmodel for databinding - this allows the bound layout access
//        // to all the data in the ViewModel
        binding.sleepTrackViewModel = viewModel
//
//        // Specify the current activity as the lifecycle owner of the binding.
//// This is used so that the binding can observe LiveData updates
        binding.setLifecycleOwner(this)
        return binding.root
    }

    private fun trackingFinished() {
        //and show user an error msg?
        viewModel.tonight.value?.let { night ->
            val nightId = night.nightId
            val action = SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(nightId)
            findNavController(this).navigate(action)
            viewModel.onTrackingFinishComplete()
        }
        //else throw an error/show a error msg to user?
    }
}
