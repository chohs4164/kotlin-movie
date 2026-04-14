package movie.domain.movie

import movie.domain.Price
import movie.domain.discount.DiscountPolicy

class Reservations(
    reservations: List<Reservation> = emptyList(),
) {
    private val reservations = reservations.toMutableList()

    fun getReservations() = reservations.toList()

    fun calculateTotalPrice(): Price =
        reservations.fold(Price(0)) { total, reservation ->
            total.sumPrice(reservation.getTotalPrice())
        }

    fun calculateDiscountedTotalPrice(discountPolicy: DiscountPolicy): Price =
        reservations.fold(Price(0)) { total, reservation ->
            total.sumPrice(reservation.calculateDiscountedPrice(discountPolicy))
        }

    fun addReservation(reservation: Reservation) {
        reservations.add(reservation)
    }

    fun getTotalPrice(): Price =
        reservations
            .map { it.getTotalPrice() }
            .fold(Price(0)) { total, price ->
                total.sumPrice(price)
            }

    fun isDupTime(movieTime: MovieTime): Boolean =
        reservations.any {
            val srcMovieTime = it.screeningMovie.movieTime
            srcMovieTime.date == movieTime.date &&
                srcMovieTime.startTime in movieTime.startTime..movieTime.endTime &&
                srcMovieTime.endTime in movieTime.startTime..movieTime.endTime
        }

    fun reset() {
        reservations.forEach {
            it.screeningMovie.deleteReservedSeats(it.seatNumbers)
        }
    }
}
