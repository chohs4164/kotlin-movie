package movie.domain.movie

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class TheaterTest {
    @Test
    fun `특정 시간에 영화 상영 요청 시 상영관 운영시간 내라면 true를 반환한다`() {
        val openTime = LocalTime.of(8, 30)
        val closeTime = LocalTime.of(20, 30)
        val theater =
            Theater(
                openTime = openTime,
                closeTime = closeTime,
            )
        val startTime = LocalTime.of(12, 30)
        val endTime = LocalTime.of(14, 30)

        Assertions
            .assertThat(theater.validateTime(startTime = startTime, endTime = endTime))
            .isTrue
    }

    @Test
    fun `특정 시간에 영화 상영 요청 시 상영관 운영시간 밖이라면 false를 반환한다`() {
        val openTime = LocalTime.of(8, 30)
        val closeTime = LocalTime.of(20, 30)
        val theater =
            Theater(
                openTime = openTime,
                closeTime = closeTime,
            )
        val startTime = LocalTime.of(7, 30)
        val endTime = LocalTime.of(14, 30)

        Assertions
            .assertThat(theater.validateTime(startTime = startTime, endTime = endTime))
            .isFalse
    }
}
