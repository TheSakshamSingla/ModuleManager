package com.saksham.modulemanager.data.model

import java.util.Date

/**
 * Represents a release of a module from a repository
 */
data class Release(
    val id: String,
    val tagName: String,
    val name: String,
    val body: String,
    val publishedAt: Date,
    val assets: List<ReleaseAsset>,
    val moduleId: String,
    val repositoryId: String,
    val isPrerelease: Boolean = false,
    val isDraft: Boolean = false
)

/**
 * Represents an asset attached to a release (typically a zip file)
 */
data class ReleaseAsset(
    val id: String,
    val name: String,
    val size: Long,
    val downloadUrl: String,
    val contentType: String,
    val createdAt: Date,
    val updatedAt: Date,
    val downloadCount: Int = 0
)
