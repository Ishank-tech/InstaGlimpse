package com.example.fawr_assignment.repository

import com.example.fawr_assignment.ApiInterface
import com.example.fawr_assignment.Models.*
import com.example.fawr_assignment.Models.storyModels.UserStoryRes
import com.example.fawr_assignment.util.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import javax.inject.Inject

class InstagramRepositoryImpl @Inject constructor(
    private val apiService: ApiInterface
) : InstagramRepository {

    override suspend fun downloadInstagramFile(url: String, cookie: String): InstagramResponse {

        val response: Response<JsonObject> = apiService.callResult(
            url,
            if (Utils.isNullOrEmpty(cookie)) "" else cookie,
            "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+"
        )

        val result = response.body()

        try {

            if (response.isSuccessful && result != null) {

                val instaResponse : InstaResponse =
                    Gson().fromJson(result, object : TypeToken<InstaResponse>() {}.type) as InstaResponse

                return InstagramResponse(isSuccess = true, downloadUrls = instaResponse)
            } else {
                return InstagramResponse(isSuccess = false, errorMessage = "No data found")
            }
        } catch (e: Exception) {
            return InstagramResponse(
                errorMessage = e.message ?: "Something Went Wrong"
            )
        }
    }

    override suspend fun downloadStory(url: String, cookie: String): InstagR {

        val response: Response<JsonObject> = apiService.callResult(
            url,
            if (Utils.isNullOrEmpty(cookie)) "" else cookie,
            "Instagram 9.5.2 (iPhone7,2; iPhone OS 9_3_3; en_US; en-US; scale=2.00; 750x1334) AppleWebKit/420+"
        )

        val result = response.body()

        try {

            if (response.isSuccessful && result != null) {

                val userStoryRes : UserStoryRes =
                    Gson().fromJson(result, object : TypeToken<UserStoryRes>() {}.type) as UserStoryRes

                return InstagR(isSuccess = true, downloadUrls = userStoryRes)
            } else {
                return InstagR(isSuccess = false, errorMessage = "No data found")
            }
        } catch (e: Exception) {
            return InstagR(
                errorMessage = e.message ?: "Something Went Wrong"
            )
        }
    }
}