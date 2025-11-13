package com.mastery.leaves.trace.core

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class TaskWorkManager {
    
    companion object {
        private const val UNIQUE_WORK_NAME = "SingleUniqueWork"
        private const val PERIODIC_WORK_NAME = "SinglePeriodicWork"
        private const val WORK_TAG = "SingleWorkTag"
        
        fun startUniqueWork(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<SingleUniqueWorker>()
                .addTag(WORK_TAG)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    UNIQUE_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            
        }
        
        fun startPeriodicWork(context: Context) {
            val periodicWorkRequest = PeriodicWorkRequestBuilder<SinglePeriodicWorker>(
                15, TimeUnit.MINUTES
            )
                .addTag(WORK_TAG)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    PERIODIC_WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWorkRequest
                )
            
        }
        
        fun cancelAllWork(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
        }
    }
}

class SingleUniqueWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        return try {

            // 执行具体的工作逻辑
            performUniqueTask()
            
            // 任务完成后启动下一个循环任务
            scheduleNextUniqueWork()
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun performUniqueTask() {
    }
    
    private fun scheduleNextUniqueWork() {
        // 延迟5分钟后启动下一个任务，实现循环
        val nextWorkRequest = OneTimeWorkRequestBuilder<SingleUniqueWorker>()
            .addTag("SingleWorkTag")
            .setInitialDelay(3, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()
        
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "SingleUniqueWork",
                ExistingWorkPolicy.REPLACE,
                nextWorkRequest
            )
        
    }
}

class SinglePeriodicWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        return try {

            performPeriodicTask()
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private fun performPeriodicTask() {
    }
}
