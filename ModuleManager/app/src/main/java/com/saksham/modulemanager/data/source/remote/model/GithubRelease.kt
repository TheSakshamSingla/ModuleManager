package com.saksham.modulemanager.data.source.remote.model

import com.google.gson.annotations.SerializedName
import com.saksham.modulemanager.data.model.Release
import com.saksham.modulemanager.data.model.ReleaseAsset
import java.util.Date

/**
 * Data model for GitHub release API response
 */
data class GithubRelease(
    val id: Long,
    @SerializedName("tag_name")
    val tagName: String,
    val name: String?,
    val body: String?,
    @SerializedName("prerelease")
    val isPrerelease: Boolean,
    @SerializedName("draft")
    val isDraft: Boolean,
    @SerializedName("published_at")
    val publishedAt: String,
    val assets: List<GithubAsset>
) {
    /**
     * Convert GitHub release to domain model
     */
    fun toDomainModel(moduleId: String, repositoryId: String): Release {
        return Release(
            id = id.toString(),
            tagName = tagName,
            name = name ?: tagName,
            body = body ?: "",
            publishedAt = parseDate(publishedAt),
            assets = assets.map { it.toDomainModel() },
            moduleId = moduleId,
            repositoryId = repositoryId,
            isPrerelease = isPrerelease,
            isDraft = isDraft
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
 * Data model for GitHub release asset
 */
data class GithubAsset(
    val id: Long,
    val name: String,
    val size: Long,
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String,
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("download_count")
    val downloadCount: Int
) {
    /**
     * Convert GitHub asset to domain model
     */
    fun toDomainModel(): ReleaseAsset {
        return ReleaseAsset(
            id = id.toString(),
            name = name,
            size = size,
            downloadUrl = browserDownloadUrl,
            contentType = contentType,
            createdAt = parseDate(createdAt),
            updatedAt = parseDate(updatedAt),
            downloadCount = downloadCount
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
