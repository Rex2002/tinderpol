package de.dhbw.tinderpol.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteNoticesDataSource {
    companion object {
        const val baseURL = "https://tinderpol.onrender.com/"
    }

    suspend fun fetchNotices(): Result<List<Notice>> {
        return withContext(Dispatchers.Default) {
            val api = Retrofit
                .Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)

            try {
                val res = api.getAll().body()
                    ?: return@withContext Result.failure(Exception("Something went wrong trying to load more Notices. Try again in a moment"))
                return@withContext Result.success(res.data)
            } catch (e: Throwable) {
                Log.e("API-Req", e.toString())
                throw e
                // return@withContext Result.failure(e)
            }
        }
    }
}