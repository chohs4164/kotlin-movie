package movie.domain.discount

import movie.domain.Price
import movie.domain.movie.MovieTime
import java.time.LocalDate
import java.time.LocalTime

class DiscountPolicy(
    private val discountCondition: DiscountCondition = DefaultDiscountCondition(),
) {
    fun calculateDiscount(
        totalPrice: Price,
        movieTime: MovieTime,
    ): Price {
        val movieDayPrice =
            calculateMovieDayDiscount(
                totalPrice = totalPrice,
                date = movieTime.date,
            )

        return calculateTimeDiscount(
            totalPrice = movieDayPrice,
            startTime = movieTime.startTime,
        )
    }

    fun calculateMovieDayDiscount(
        totalPrice: Price,
        date: LocalDate,
    ): Price {
        require(!discountCondition.isMovieDay(date = date)) {
            val discountPrice = Price((totalPrice.value * 0.1).toInt())

            return totalPrice.minusPrice(discountPrice)
        }

        return totalPrice
    }

    fun calculateTimeDiscount(
        totalPrice: Price,
        startTime: LocalTime,
    ): Price {
        if (discountCondition.isDiscountTime(startTime = startTime)) {
            val discountPrice = Price(2000)

            return totalPrice.minusPrice(discountPrice)
        }

        return totalPrice
    }
}
