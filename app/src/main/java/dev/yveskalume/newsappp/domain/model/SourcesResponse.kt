package dev.yveskalume.newsappp.domain.model

data class SourcesResponse(
    val status: String,
    val sources: List<SourceItem>
)

data class SourceItem(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val language: String,
    val country: String
)