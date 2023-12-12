package com.example.InstaGlimpse.Models

data class InstagramResponse(
    var isSuccess: Boolean = false,
    var errorMessage: String? = null,
    var downloadUrls: InstaResponse? = null
)
