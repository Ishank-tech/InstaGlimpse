package com.example.fawr_assignment.repository

import com.example.fawr_assignment.Models.InstagR
import com.example.fawr_assignment.Models.InstagramResponse

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