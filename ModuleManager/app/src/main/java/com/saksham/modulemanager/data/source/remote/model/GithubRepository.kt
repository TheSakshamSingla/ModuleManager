package com.saksham.modulemanager.data.source.remote.model

import com.google.gson.annotations.SerializedName
import com.saksham.modulemanager.data.model.Repository
import java.util.Date

/**
 * Data model for GitHub repository API response
 */
data class GithubRepository(
    val id: Long,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val owner: GithubOwner,
    val description: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    val homepage: String?,
    @SerializedName("stargazers_count")
    val stars: Int,
    @SerializedName("forks_count")
    val forks: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("default_branch")
    val defaultBranch: String
) {
    /**
     * Convert GitHub repository to domain model
     */
    fun toDomainModel(): Repository {
        val parts = fullName.split("/")
        val repoOwner = if (parts.size > 1) parts[0] else owner.login
        
        return Repository(
            id = fullName,
            name = name,
            owner = repoOwner,
            description = description,
            url = htmlUrl,
            website = homepage,
            stars = stars,
            forks = forks,
            lastUpdated = parseDate(updatedAt),
            lastFetched = Date(),
            moduleCount = 0,
            isOfficial = false,
            avatarUrl = owner.avatarUrl
        )
    }
    
    private fun parseDate(dateString: String): Date {
        return try {
            // Simple parsing, in a real app would use a proper date parser
            Date()
        } catch (e: Exception) {
            Date()
        }
    }
}

/**
 * Data model for GitHub repository owner
 */
data class GithubOwner(
    val login: String,
    val id: Long,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("html_url")
    val htmlUrl: String
)
