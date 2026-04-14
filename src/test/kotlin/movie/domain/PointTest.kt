package movie.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class PointTest {
    @Test
    fun `포인트가 0이하라면 예외가 발생된다`() {
        assertThrows<IllegalArgumentException> {
            val point = Point(-100)
        }
    }

    @Test
    fun `포인트보다 큰 포인트를 사용하려고 하면 예외가 발생한다`() {
        val point = Point(1000)
        val usePoint = Point(1001)

        assertThrows<IllegalArgumentException> {
            point.use(usePoint)
        }
    }

    @Test
    fun `보유 포인트보다 낮은거나 같은 포인트를 사용하려고 하면 포인트가 정상적으로 반환된다`() {
        val point = Point(1000)
        val usePoint = Point(100)

        assertDoesNotThrow {
            val newPoint = point.use(usePoint)
        }
    }
}
