package com.example.fawr_assignment.Models

import com.example.fawr_assignment.Models.storyModels.UserStoryRes

data class InstagR(
    var isSuccess: Boolean = false,
    var errorMessage: String? = null,
    var downloadUrls: UserStoryRes? = null
)