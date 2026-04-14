package movie.domain.point

import movie.domain.Point
import movie.domain.Price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PointPolicyTest {
    @Test
    fun `포인트를 사용할 경우 할인된 금액을 반환한다`() {
        val pointPolicy = PointPolicy()

        val totalPrice = Price(20_000)
        val usePoint = Point(1000)

        val resultPrice =
            pointPolicy.usePoint(
                totalPrice = totalPrice,
                usePoint = usePoint,
            )

        assertThat(resultPrice.value).isEqualTo(19_000)
    }
}
