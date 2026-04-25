package movie.domain.movie

interface ReservationRepository {
    fun saveAll(reservations: List<Reservation>)
}
