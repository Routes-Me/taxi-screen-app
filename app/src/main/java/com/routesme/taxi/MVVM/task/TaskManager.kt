package com.routesme.taxi.MVVM.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TaskManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {

        return Result.success()

    }

}