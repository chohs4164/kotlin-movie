package movie.domain.movie

import movie.domain.Price
import movie.domain.discount.DiscountPolicy
import movie.domain.seat.number.SeatNumber

class ReservationCart(
    private val reservations: Reservations = Reservations(),
) {
    fun calculateDiscountedTotalPrice(discountPolicy: DiscountPolicy): Price = reservations.calculateDiscountedTotalPrice(discountPolicy)

    fun getReservations(): List<Reservation> = reservations.getReservations()

    fun resetSeat() {
        reservations.reset()
    }

    fun addReservation(
        screeningMovie: ScreeningMovie,
        seatNumbers: List<SeatNumber>,
    ): Reservation {
        require(!reservations.isDupTime(screeningMovie.movieTime)) {
            "선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요."
        }

        screeningMovie.reserve(seatNumbers)

        val reservation =
            Reservation(
                screeningMovie = screeningMovie,
                seatNumbers = seatNumbers,
            )

        reservations.addReservation(reservation)

        return reservation
    }
}
