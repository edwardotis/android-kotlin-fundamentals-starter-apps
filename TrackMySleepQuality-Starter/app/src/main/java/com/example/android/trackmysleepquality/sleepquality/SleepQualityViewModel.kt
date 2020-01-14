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

package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.*
import timber.log.Timber

class SleepQualityViewModel(val database: SleepDatabaseDao, private val sleepNightKey: Long) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }
    //pass id in bundleArgs or pull from db? Or just update in
    // a txn and call get then update, if it's never part of the UI
//    private val tonight =
    init {

    }

    fun onClickSleepQuality(quality: Int) {
        Timber.i("onClickSleepQuality called")
        //parse int out string w/ int to save
        //to db in coroutine
        uiScope.launch {
            // IO is a thread pool for running operations that access the disk, such as
            // our Room database.
            withContext(Dispatchers.IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.updateNight(tonight)
            }
            // Setting this state variable to true will alert the observer and trigger navigation.
            _navigateToSleepTracker.value = true
        }

    }

    //DAO WRAPPERS
//    private suspend fun update(night: SleepNight) {
//        withContext(Dispatchers.IO) {
//            database.updateNight(night)
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("ViewModel destroyed!")
    }
}