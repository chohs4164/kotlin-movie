package movie.domain.discount

import java.time.LocalDate
import java.time.LocalTime

class DefaultDiscountCondition : DiscountCondition {
    override fun isMovieDay(date: LocalDate): Boolean = (date.dayOfMonth in setOf(10, 20, 30))

    override fun isDiscountTime(startTime: LocalTime): Boolean =
        (
                startTime < LocalTime.of(11, 0, 0) ||
                        startTime > LocalTime.of(20, 0, 0)
                )
}
