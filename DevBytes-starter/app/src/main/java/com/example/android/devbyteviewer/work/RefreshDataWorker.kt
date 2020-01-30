package com.example.android.devbyteviewer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.devbyteviewer.database.getDatabase

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
        CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        //TODO What's best way to handle CoroutineScope with BoundaryCallback
        //I guess not tieing it to a viewmodel apparently.
//        val repository = VideosRepository(database)
//        try {
//            Timber.d("Work request for sync is run")
//            repository.refreshVideos()
//        } catch (e: HttpException) {
//            Timber.w("Work request failures: ${e}")
//            return Result.retry()
//        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "com.example.android.devbyteviewer.work.RefreshDataWorker"
    }
}