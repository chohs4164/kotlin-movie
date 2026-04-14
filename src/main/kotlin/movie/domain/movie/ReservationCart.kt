package movie.domain.movie

import movie.domain.Price
import movie.domain.discount.DiscountPolicy
import movie.domain.seat.number.SeatNumber

class ReservationCart(
    private val reservations: Reservations = Reservations(),
) {
    fun calculateDiscountedTotalPrice(discountPolicy: DiscountPolicy): Price = reservations.calculateDiscountedTotalPrice(discountPolicy)

    fun getReservations(): List<Reservation> = reservations.getReservations()

    fun isDupTime(movieTime: MovieTime): Boolean = reservations.isDupTime(movieTime = movieTime)

    fun resetSeat() {
        reservations.reset()
    }

    fun addReservation(
        screeningMovie: ScreeningMovie,
        seatNumbers: List<SeatNumber>,
    ): Reservation {
        val reservation =
            Reservation(
                screeningMovie = screeningMovie,
                seatNumbers = seatNumbers,
            )

        reservations.addReservation(reservation)
        screeningMovie.addReservedSeats(seatNumbers)

        return reservation
    }
}
