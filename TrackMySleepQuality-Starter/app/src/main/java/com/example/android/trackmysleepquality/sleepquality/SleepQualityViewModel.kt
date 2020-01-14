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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*
import timber.log.Timber

class SleepQualityViewModel(val sleepDao: SleepDatabaseDao, application:
Application) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //pass id in bundleArgs or pull from db? Or just update in
    // a txn and call get then update, if it's never part of the UI
//    private val tonight =
    init {

    }

    fun onClickSleepQuality(qualityDesc: String) {
        Timber.i("onClickSleepQuality called")
        //parse int out string w/ int to save
        //to db in coroutine
        uiScope.launch {

        }

    }

    //DAO WRAPPERS
    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            sleepDao.updateNight(night)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("ViewModel destroyed!")
    }
}