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

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.DevByteVideo
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 */
class VideosRepository(private val database: VideosDatabase, private val coroutineScope: CoroutineScope) {

    /**
     * given the observer on the chain:
     * dsFactoryDatabaseVideos -> dsFactoryDevByteVideos -> videos below
     *
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     */
    fun refreshVideos(): LiveData<PagedList<DevByteVideo>> {
        /**
         * Construct a boundary callback object each time
         */
        Timber.d("refresh videos is called")
        val boundaryCallback = VideosBoundaryCallback(database, coroutineScope)
        val dsFactoryDatabaseVideos = database.videoDao.getVideos()
        val dsFactoryDevByteVideos = dsFactoryDatabaseVideos.mapByPage { dbVideo ->
            dbVideo.asDomainModel()
        }
        // Get the paged list
        val videos = LivePagedListBuilder(dsFactoryDevByteVideos, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()


        return videos
    }

    /**
     * OK major changes coming in here to support boundary callback
     * com/example/android/codelabs/paging/data/GithubRepository.kt
     */
//    private val dsFactoryDatabaseVideos = database.videoDao.getVideos()
//    //This line converts from DatabaseVideo into DevByteVideo. Cannot use Transformation on DataSourceFactorys
//    private val dsFactoryDevByteVideos = dsFactoryDatabaseVideos.mapByPage { dbVideo ->
//        dbVideo.asDomainModel()
//    }
//    val videos = LivePagedListBuilder(dsFactoryDevByteVideos, DATABASE_PAGE_SIZE).build()

    /**
     * realistically, the network size should be biggest b/c it takes waaay longer than db query to return
     */
    companion object {
        const val NETWORK_PAGE_SIZE = 15
        private const val DATABASE_PAGE_SIZE = 10
    }

}