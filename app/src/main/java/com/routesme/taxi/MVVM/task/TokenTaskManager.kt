package com.routesme.taxi.MVVM.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TokenTaskManager(context: Context, params: WorkerParameters): Worker(context, params){
    override fun doWork(): Result {

        return Result.success()

    }

}