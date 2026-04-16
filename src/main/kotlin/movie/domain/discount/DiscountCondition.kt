package movie.domain.discount

import java.time.LocalDate
import java.time.LocalTime

interface DiscountCondition {
    fun isMovieDay(date: LocalDate): Boolean

    fun isDiscountTime(startTime: LocalTime): Boolean
}
