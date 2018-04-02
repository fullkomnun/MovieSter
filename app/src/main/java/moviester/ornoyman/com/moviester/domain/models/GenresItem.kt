package moviester.ornoyman.com.moviester.domain.models

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class GenresItem(
	val name: String? = null,
	val id: Int? = null
)
