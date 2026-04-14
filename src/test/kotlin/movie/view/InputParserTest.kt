package movie.view

import movie.domain.Point
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class InputParserTest {
    @Test
    fun `Y는 true를 반환한다`() {
        val result = InputParser.parseYesNo("Y")

        assertThat(result).isTrue
    }

    @Test
    fun `N는 false를 반환한다`() {
        val result = InputParser.parseYesNo("F")

        assertThat(result).isFalse
    }

    @Test
    fun `날짜는 LocalDate로 변환하여 반환한다`() {
        val result = InputParser.parseDate("2026-04-09")
        val date = LocalDate.of(2026, 4, 9)

        assertThat(result).isEqualTo(date)
    }

    @Test
    fun `숫자는 Int타입으로 변환하여 반환한다`() {
        val result = InputParser.parseNumber("10")

        assertThat(result).isEqualTo(10)
    }

    @Test
    fun `포인트는 Point 객체로 변환하여 반환한다`() {
        val result = InputParser.parsePoint("1000")
        val point = Point(1000)

        assertThat(result).isEqualTo(point)
    }

    @Test
    fun `좌석 번호를 파싱하고, 좌석 번호 리스트를 반환한다`() {
        val result = InputParser.parseSeatNumbers("A1, B1")
        val seatNumbers =
            listOf(
                SeatNumber(
                    row = Row('A'),
                    col = Column(1),
                ),
                SeatNumber(
                    row = Row('B'),
                    col = Column(1),
                ),
            )

        assertThat(result).isEqualTo(seatNumbers)
    }
}
