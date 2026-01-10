package dev.yveskalume.newsappp.data.network.model

import dev.yveskalume.newsappp.domain.model.SourcesResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourcesResponseDto(
    @SerialName("status")
    val status: String,
    @SerialName("sources")
    val sources: List<SourceItemDto>? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("message")
    val message: String? = null
)

fun SourcesResponseDto.toDomain(): SourcesResponse {
    return SourcesResponse(
        status = status,
        sources = sources?.mapNotNull { it.toDomain() } ?: emptyList()
    )
}