package movie.domain.movie

import java.time.LocalDate
import java.time.LocalTime

class MovieTime(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    fun checkDuplicate(movieTime: MovieTime): Boolean {
        if (date != movieTime.date) {
            return false
        }

        return (movieTime.startTime in startTime..endTime) && (movieTime.endTime in startTime..endTime)
    }
}
