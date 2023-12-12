package com.example.InstaGlimpse.repository

import com.example.InstaGlimpse.Models.InstagR
import com.example.InstaGlimpse.Models.InstagramResponse

interface InstagramRepository {
    suspend fun downloadInstagramFile(
        url: String,
        cookie: String
    ): InstagramResponse

    suspend fun downloadStory(
        url: String,
        cookie: String
    ): InstagR

}