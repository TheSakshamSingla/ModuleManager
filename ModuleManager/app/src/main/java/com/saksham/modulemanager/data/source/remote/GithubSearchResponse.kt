package com.saksham.modulemanager.data.source.remote

import com.saksham.modulemanager.data.source.remote.model.GithubRepository

/**
 * Response model for GitHub repository search
 */
data class GithubSearchResponse(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<GithubRepository>
)
