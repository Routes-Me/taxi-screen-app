package com.routesme.taxi.worker

import android.content.Context
import androidx.work.*

class TaskManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        return Result.success()

    }

}