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
        val previousAutoCommit = connection.autoCommit
        connection.autoCommit = false

        try {
            reservations.forEach(::save)
            connection.commit()
        }catch(e: IllegalArgumentException){
            connection.rollback()
            throw e
        }catch(e: SQLException){
            connection.rollback()
            if(e.isDuplicateSeatException()){
                throw IllegalArgumentException("이미 예약된 좌석입니다.")
            }
            throw e
        }finally {
            connection.autoCommit = previousAutoCommit
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun save(reservation: Reservation) {
        val screeningId =
            requireNotNull(reservation.screeningMovie.screeningId){
                "상영 정보를 찾을 수 없습니다."
            }


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

    private fun SQLException.isDuplicateSeatException(): Boolean =
        generateSequence<Throwable>(this) { throwable ->
            when (throwable) {
                is SQLException -> throwable.nextException ?: throwable.cause
                else -> throwable.cause
            }
        }.filterIsInstance<SQLException>()
            .any { it.sqlState == "23505" }
}