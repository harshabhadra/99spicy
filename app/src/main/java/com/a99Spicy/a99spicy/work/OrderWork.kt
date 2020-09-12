package com.a99Spicy.a99spicy.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.a99Spicy.a99spicy.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class OrderWork(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val apiService = Api.retrofitService

//        val order = null
//        withContext(Dispatchers.IO) {
//            val responseDeferred = apiService.placeOrderAsync(order)
//            try {
//                val response = responseDeferred.await()
//            } catch (e: Exception) {
//                Timber.e("Failed to place order: ${e.message}")
//            }
//        }
        return Result.success()
    }

}