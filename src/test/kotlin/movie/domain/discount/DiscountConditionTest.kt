package movie.domain.discount

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class DiscountConditionTest {
    @Test
    fun `무비데이 할인 조건에 해당하면 true를 반환한다`() {
        val discountCondition = DiscountCondition()
        val movieDate = LocalDate.of(2026, 4, 10)

        val isMovieDay = discountCondition.isMovieDay(date = movieDate)

        assertThat(isMovieDay).isTrue
    }

    @Test
    fun `무비데이 할인 조건에 해당하지 않으면 false를 반환한다`() {
        val discountCondition = DiscountCondition()
        val movieDate = LocalDate.of(2026, 4, 11)

        val isMovieDay = discountCondition.isMovieDay(date = movieDate)

        assertThat(isMovieDay).isFalse
    }

    @Test
    fun `타임 할인 조건에 해당하면 true를 반환한다`() {
        val discountCondition = DiscountCondition()
        val movieTime = LocalTime.of(7, 0, 0)

        val isTime = discountCondition.isTime(startTime = movieTime)

        assertThat(isTime).isTrue
    }

    @Test
    fun `타임 할인 조건에 해당하지 않으면 false를 반환한다`() {
        val discountCondition = DiscountCondition()
        val movieTime = LocalTime.of(15, 0, 0)

        val isTime = discountCondition.isTime(startTime = movieTime)

        assertThat(isTime).isFalse
    }
}
