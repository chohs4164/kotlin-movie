package movie.domain.movie

class Theaters(
    theaters: List<Theater>,
) {
    private val theaters = theaters.toMutableList()

    fun addTheater(newTheater: Theater) {
        theaters.add(newTheater)
    }
}
