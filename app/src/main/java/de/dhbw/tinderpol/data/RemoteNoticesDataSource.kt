package de.dhbw.tinderpol.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

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
                    val res = api.getAll()
                    if (res.isSuccessful) {
                        val body = res.body()
                        if (body == null || body.err != null) {
                            Log.e("API-Req", body?.err ?: "Unknown error.")
                            return@withContext Result.failure(
                                Exception(
                                    body?.err
                                        ?: "Something went wrong trying to load more Notices. Try again in a moment."
                                )
                            )
                        }
                        else return@withContext Result.success(body.data)
                    } else {
                        Log.e("API-Req", res.code().toString())
                        Log.e("API-Req", res.message())
                        return@withContext Result.failure(Exception(res.message()))
                    }
                } catch (e: SocketTimeoutException) {
                    Log.e("API-Req", "Timeout... Trying again.")
                    return@withContext fetchNotices()
                } catch (e: Throwable) {
                    Log.e("API-Req", e.message ?: "Unknown Error.")
                    return@withContext Result.failure(e)
                }
        }
    }
}