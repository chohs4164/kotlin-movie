package movie.infrastructure.db

import movie.domain.movie.Reservation
import java.sql.Connection
import java.sql.SQLException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class JdbcReservationRepository(
    private val connection: Connection,
) {
    fun saveAll(reservations: List<Reservation>) {
        reservations.forEach(::save)
    }

    @OptIn(ExperimentalUuidApi::class)
    fun save(reservation: Reservation) {
        val screeningId = findScreeningId(reservation)

        val sql =
            """
            insert into seats (seats_id, screening_id, seat_number)
            values (?, ?, ?)
            """.trimIndent()

        try {
            connection.prepareStatement(sql).use { statement ->
                reservation.seatNumbers.forEach { seatNumber ->
                    statement.setString(1, Uuid.random().toString())
                    statement.setString(2, screeningId)
                    statement.setString(3, seatNumber.toString())
                    statement.addBatch()
                }
                statement.executeBatch()
            }
        } catch (e: SQLException) {
            if (e.isDuplicateSeatException()) {
                throw IllegalArgumentException("이미 예약된 좌석입니다.")
            }
            throw e
        }
    }

    private fun findScreeningId(reservation: Reservation): String {
        val screeningMovie = reservation.screeningMovie
        val sql =
            """
            select s.screening_id
            from screenings s
            join movies m on s.movie_id = m.movie_id
            join theaters t on s.theater_id = t.theater_id
            where m.title = ?
                and t.open_time = ?
                and t.close_time = ?
                and s.screening_date = ?
                and s.start_time = ?
                and s.end_time = ?
            """.trimIndent()

        return connection.prepareStatement(sql).use { statement ->
            statement.setString(1, screeningMovie.movie.title.value)
            statement.setObject(2, screeningMovie.theater.openTime)
            statement.setObject(3, screeningMovie.theater.closeTime)
            statement.setObject(4, screeningMovie.movieTime.date)
            statement.setObject(5, screeningMovie.movieTime.startTime)
            statement.setObject(6, screeningMovie.movieTime.endTime)

            statement.executeQuery().use { resultSet ->
                require(resultSet.next()) { "상영 정보를 찾을 없습니다." }
                resultSet.getString("screening_id")
            }
        }
    }

    private fun SQLException.isDuplicateSeatException(): Boolean =
        generateSequence<Throwable>(this) { throwable ->
            when (throwable) {
                is SQLException -> throwable.nextException ?: throwable.cause
                else -> throwable.cause
            }
        }.filterIsInstance<SQLException>()
            .any { it.sqlState == "23505" }
}