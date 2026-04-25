package movie.application

data class MovieCatalogOrder(
    val titles: List<String>,
) {
    fun indexOf(title: String): Int = titles.indexOf(title).takeIf { it >= 0 } ?: Int.MAX_VALUE
}
