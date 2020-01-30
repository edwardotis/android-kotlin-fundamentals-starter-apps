package com.example.android.devbyteviewer.repository

import androidx.paging.PagedList
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.domain.DevByteVideo
import com.example.android.devbyteviewer.network.DevByteNetwork
import com.example.android.devbyteviewer.network.asDatabaseModel
import com.example.android.devbyteviewer.repository.VideosRepository.Companion.NETWORK_PAGE_SIZE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * TODO I think the type should be the class used in the recyclerview, but we'll see
 * NOPE, needed to match the dao return type, which provides the datasourceFactory object
 * com.example.android.devbyteviewer.viewmodels.DevByteViewModel.getPlaylist
 */
class VideosBoundaryCallback(private val database: VideosDatabase,
                             private val viewModelScope: CoroutineScope) :
        PagedList.BoundaryCallback<DevByteVideo>() {

    //TODO If we add a query param from user, then currentPage needs to be reset internally.
    // That's why I'm not making this a param for refreshVideos
    var currentPage = 0

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        Timber.d("onZeroItemsLoaded called.")
        refreshVideos()
    }

    override fun onItemAtEndLoaded(itemAtEnd: DevByteVideo) {
        super.onItemAtEndLoaded(itemAtEnd)
        currentPage = itemAtEnd.page
        //TODO so we need to either manually track lastRequestedPage or else
        //use some info in this itemAtEnd object to determine the next
        //network request to make to with DevByteNetwork
        //Makes sense that we were in an infinite loop before without handing this.
        // @see com.example.android.codelabs.paging.data.RepoBoundaryCallback#lastRequestedPage
        Timber.d("onItemAtEndLoaded called. BUG we keep getting same list in infinite loop")
        refreshVideos()
    }

    override fun onItemAtFrontLoaded(itemAtFront: DevByteVideo) {
        super.onItemAtFrontLoaded(itemAtFront)
        Timber.d("onItemAtFrontLoaded called. Doing nothing")
    }

    private fun refreshVideos() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Timber.d("refresh videos is called")
                //TODO switch to an api that supports paginated network requests
                //            val playlist = DevByteNetwork.devbytes.getPlaylist(1, NETWORK_PAGE_SIZE).await()
                val playlist = DevByteNetwork.devbytes.getPlaylist(currentPage + 1, NETWORK_PAGE_SIZE).await()
                database.videoDao.insertAll(playlist.photos.asDatabaseModel())
            }
        }
    }

}