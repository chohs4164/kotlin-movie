package movie.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class PriceTest {
    @Test
    fun `Price가 음수일 경우 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            Price(-1000)
        }
    }

    @Test
    fun `Price가 0이상일 경우 Price가 생성된다`() {
        assertDoesNotThrow {
            Price(0)
            Price(1000)
        }
    }

    @Test
    fun `Price 덧셈이 성공한다`() {
        val price1 = Price(0)
        val price2 = Price(1000)

        assertDoesNotThrow {
            price1.sumPrice(price2)
        }
    }
}
