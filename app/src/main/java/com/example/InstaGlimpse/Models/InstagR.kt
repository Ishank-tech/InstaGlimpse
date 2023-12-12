package com.example.InstaGlimpse.Models

import com.example.InstaGlimpse.Models.storyModels.UserStoryRes

data class InstagR(
    var isSuccess: Boolean = false,
    var errorMessage: String? = null,
    var downloadUrls: UserStoryRes? = null
)