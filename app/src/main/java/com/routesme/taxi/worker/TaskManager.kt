package com.routesme.taxi.worker

import android.R
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.routesme.taxi.view.events.WorkReport
import org.greenrobot.eventbus.EventBus

class TaskManager(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private val TAG = "SEND_ANALYTICS_REPORT"
    override fun doWork(): Result {
        try {

            EventBus.getDefault().post(WorkReport("SUCCESS"))
            return Result.success()
        }catch (e:Exception){
            return Result.failure()
        }

    }

}
