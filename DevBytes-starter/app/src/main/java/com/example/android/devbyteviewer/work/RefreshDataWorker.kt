package com.example.android.devbyteviewer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.devbyteviewer.database.getDatabase
import com.example.android.devbyteviewer.repository.VideosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.HttpException
import timber.log.Timber

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
        CoroutineWorker(appContext, params) {
    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val refreshDataWorkerJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val refreshDataWorkerScope = CoroutineScope(refreshDataWorkerJob + Dispatchers.Main)

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        //TODO What's best way to handle CoroutineScope with BoundaryCallback
        //I guess not tieing it to a viewmodel apparently.
//        Need to figure out coroutinescope for Worker calls.
//        Probably Worker should own the scope and cancel it when killed.
        val repository = VideosRepository(database, refreshDataWorkerScope)
        try {
            Timber.d("Work request for sync is run")
            repository.refreshVideos()
        } catch (e: HttpException) {
            Timber.w("Work request failures: ${e}")
            return Result.retry()
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "com.example.android.devbyteviewer.work.RefreshDataWorker"
    }
}