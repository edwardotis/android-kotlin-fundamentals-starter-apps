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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*
import timber.log.Timber

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val sleepDao: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var _tonight = MutableLiveData<SleepNight?>()
    val tonight: LiveData<SleepNight?>
        get() = _tonight

    //TODO is this really ok to setup this LiveData without a coroutine/async wrapper?
    //oh, I think that's part of beauty of LiveData and Room vs the others
    //that return Entity directly
    private var nights = sleepDao.getAllNights()
    val nightsString = Transformations.map(nights) { theNights ->
        formatNights(theNights, application.resources)
    }

    // I don't think the tutorial's version was necessarily better than this way
    // and reading the viewModel.tonight value in fragment. eh, I guess their way
    // pushed more data into view, instead of pulling it from model. But, eh.
    // Many ways to handle it.
    //event which triggers end of the game
    private val _eventTrackingFinished = MutableLiveData<Boolean>()
    val eventTrackingFinished: LiveData<Boolean>
        get() = _eventTrackingFinished

    //BUTTON visibility
    val startButtonVisible = Transformations.map(tonight) {
        it == null
    }
    val stopButtonVisible = Transformations.map(tonight) {
        it != null
    }
    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }
    init {
        initializeTonight()
        _eventTrackingFinished.value = false
        //aha, if we have an active tonight var, then we should init
        //start inactive button. This would be mapping from one liveData
        //to another with Transformation, right?
    }

    private fun initializeTonight() {
        uiScope.launch {
            _tonight.value = getTonightFromDatabase()
        }
    }

//    private fun initializeNights() {
//        uiScope.launch {
//            nights = sleepDao.getAllNights()
//        }
//    }

    /**
     * Create and Save a SleepNight into dao.
     * Deactivate start button. (Let's see their recommendation)
     * Activate stop button
     *
     * Activate clear button yet? Guess so.
     */
    fun onStartTracking() {
        Timber.i("onStart Called")
        uiScope.launch {
            val newNight = SleepNight()
            addNight(newNight)
            _tonight.value = getTonightFromDatabase()
        }
    }

    fun onStopTracking() {
        Timber.i("onStopTracking Called")
        uiScope.launch {
            //TODO how to change value of tonight endTime?
            val oldNight = _tonight.value ?: return@launch
            oldNight.endTimeMilli = java.lang.System.currentTimeMillis()
            update(oldNight)
            //update stop and start button or no b/c we're going to new fragment
        }
        finishTracking()
    }

    private fun finishTracking() {
        _eventTrackingFinished.value = true
    }

    fun onTrackingFinishComplete() {
        _eventTrackingFinished.value = false
    }


    //DAO Wrappers
    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            sleepDao.updateNight(night)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            sleepDao.clear()
        }
    }

    //    private suspend  fun getNights():LiveData<List<SleepNight>>{
//        return withContext(Dispatchers.IO){
//            nights = getNights()
//        }
//    }
//
    private suspend fun addNight(night: SleepNight) {
        withContext(Dispatchers.IO) {
            sleepDao.addNight(night)
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var tonight = sleepDao.getTonight()
            //this checks if the tonight var is active. i.e. hasn't yet been stopped, which
            //would mean most recent night in db is from a prior, completed night
            if (tonight?.endTimeMilli != tonight?.startTimeMilli) {
                tonight = null
            }
            tonight
        }
    }

//    fun onStop() {
//        Timber.i("onStop Called")
//        _eventTrackingFinished.value = true
//
//    }

    fun onClear() {
        Timber.i("onClear Called")
        uiScope.launch {
            clear()
            _tonight.value = null//Disabled stop button since active night is deleted
            _showSnackbarEvent.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("ViewModel destroyed!")
    }
}

