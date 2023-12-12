package com.example.InstaGlimpse

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface ApiInterface {
    @GET
    suspend fun callResult(
        @Url value: String,
        @Header("Cookie") cookie: String,
        @Header("User-Agent") userAgent: String
    ): Response<JsonObject>
}