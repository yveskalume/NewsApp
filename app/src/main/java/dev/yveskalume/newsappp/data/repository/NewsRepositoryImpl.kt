package dev.yveskalume.newsappp.data.repository

import dev.yveskalume.newsappp.data.network.datasource.NewsDataSource
import dev.yveskalume.newsappp.data.network.model.toDomain
import dev.yveskalume.newsappp.domain.model.NewsResponse

class NewsRepositoryImpl(
    private val newsDataSource: NewsDataSource
) : NewsRepository {

    override suspend fun getTopHeadlines(
        query: String?,
        sources: String?,
        pageSize: Int,
        page: Int
    ): Result<NewsResponse> = runCatching {
        newsDataSource.getTopHeadlines(
            query = query,
            sources = sources,
            pageSize = pageSize,
            page = page
        ).toDomain()
    }
}
