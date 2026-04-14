package movie.domain.movie

@JvmInline
value class MovieTitle(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "영화 제목은 공백일 수 없습니다." }
    }
}
