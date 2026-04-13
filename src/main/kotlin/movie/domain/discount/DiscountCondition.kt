package movie.domain.discount

import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime

interface DiscountCondition {
    fun isMovieDay(date: LocalDate): Boolean
    fun isTime(startTime: LocalTime): Boolean
}