package dev.yveskalume.newsappp.data.repository

import dev.yveskalume.newsappp.data.network.datasource.SourcesDataSource
import dev.yveskalume.newsappp.data.network.model.toDomain
import dev.yveskalume.newsappp.domain.model.SourcesResponse

class SourcesRepositoryImpl(
    private val sourcesDataSource: SourcesDataSource
) : SourcesRepository {

    override suspend fun getSources(category: String?): Result<SourcesResponse> = runCatching {
        sourcesDataSource.getSources(category = category).toDomain()
    }
}
