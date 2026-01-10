package dev.yveskalume.newsappp.data.repository

import dev.yveskalume.newsappp.domain.model.SourcesResponse

interface SourcesRepository {
    suspend fun getSources(
        category: String? = null
    ): Result<SourcesResponse>
}
