package movie.view

import movie.domain.Point
import movie.domain.movie.MovieTitle
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import java.time.LocalDate

object InputParser {
    fun parseYesNo(input: String): Boolean = input.uppercase() == "Y"

    fun parseDate(input: String): LocalDate = LocalDate.parse(input)

    fun parseNumber(input: String): Int = input.toInt()

    fun parsePoint(input: String): Point = Point(input.toInt())

    fun parseSeatNumbers(input: String): List<SeatNumber> =
        input
            .split(",")
            .map { it.trim() }
            .map { seat ->
                val row = seat.first()
                val col = seat.substring(1).toInt()

                SeatNumber(
                    row = Row(row),
                    col = Column(col),
                )
            }

    fun parseMovieTitle(input: String): MovieTitle = MovieTitle(input)

    fun parseIndex(
        input: String,
        size: Int,
    ): Int {
        val index = input.toInt()
        require(index in 0..size) { "유효한 번호를 입력해주세요" }

        return index - 1
    }
}
