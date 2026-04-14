package movie.domain.movie

import movie.domain.Price
import movie.domain.discount.DiscountPolicy
import movie.domain.seat.number.SeatNumber

class Reservation(
    val screeningMovie: ScreeningMovie,
    val seatNumbers: List<SeatNumber>,
) {
    fun calculateDiscountedPrice(discountPolicy: DiscountPolicy): Price =
        discountPolicy.calculateDiscount(
            totalPrice = getTotalPrice(),
            movieTime = screeningMovie.movieTime,
        )

    fun getTotalPrice(): Price = screeningMovie.calculatePrice(targetSeatNumbers = seatNumbers)
}
