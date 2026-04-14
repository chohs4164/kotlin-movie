package movie.view

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class InputValidatorTest {
    @Test
    fun `Y나 N를 제외한 값을 받으면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateYesNo("YY")
        }
    }

    @Test
    fun `날짜 형식에 맞지 않다면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateDate("25-1-1")
        }
    }

    @Test
    fun `숫자가 아닌 값을 받으면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateNumber("일")
        }
    }

    @Test
    fun `좌석 번호 형식에 맞지 않다면 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            InputValidator.validateSeatNumbers("좌석번호인데")
        }
    }

    @Test
    fun `Y나 N를 값을 받으면 예외가 발생하지 않는다`() {
        assertDoesNotThrow {
            InputValidator.validateYesNo("Y")
            InputValidator.validateYesNo("N")
        }
    }

    @Test
    fun `날짜 형식에 맞다면 예외가 발생하지 않는다`() {
        assertDoesNotThrow {
            InputValidator.validateDate("2025-01-01")
        }
    }

    @Test
    fun `숫자를 받으년 예외가 발생하지 않는다`() {
        assertDoesNotThrow {
            InputValidator.validateNumber("1")
        }
    }

    @Test
    fun `좌석 번호 형식에 맞다면 예외가 발생하지 않는다`() {
        assertDoesNotThrow {
            InputValidator.validateSeatNumbers("A1, B1")
        }
    }
}
