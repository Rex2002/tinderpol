package de.dhbw.tinderpol.data

import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("/all")
    suspend fun getAll(): Response<APIResponse>
}