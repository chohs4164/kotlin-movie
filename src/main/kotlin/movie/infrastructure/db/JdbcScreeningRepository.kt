package movie.infrastructure.db

import movie.domain.movie.Movie
import movie.domain.movie.MovieTime
import movie.domain.movie.MovieTitle
import movie.domain.movie.ScreeningMovie
import movie.domain.movie.Theater
import movie.domain.seat.number.Column
import movie.domain.seat.number.Row
import movie.domain.seat.number.SeatNumber
import java.sql.Connection
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class MovieScreeningRecord(
    val movieId: String,
    val title: String,
    val screeningId: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
)

class JdbcScreeningRepository(
    private val connection: Connection,
) {
    @OptIn(ExperimentalUuidApi::class)
    fun save(screeningMovie: ScreeningMovie) {
        val movieId =
            findMovieIdByTitle(screeningMovie.movie.title)
                ?: Uuid.random().toString().also { saveMovie(it, screeningMovie.movie) }
        val theaterId = Uuid.random().toString()
        val screeningId = Uuid.random().toString()

        saveTheater(theaterId, screeningMovie.theater)
        saveScreening(
            screeningId = screeningId,
            movieId = movieId,
            theaterId = theaterId,
            movieTime = screeningMovie.movieTime,
        )
    }

    fun saveAll(screeningMovies: List<ScreeningMovie>) {
        screeningMovies.forEach(::save)
    }

    fun existsByTitle(title: MovieTitle): Boolean {
        val sql =
            """
            select count(*)
            from movies
            where title = ?
            """.trimIndent()

        return connection.prepareStatement(sql).use { statement ->
            statement.setString(1, title.value)

            statement.executeQuery().use { resultSet ->
                resultSet.next()
                resultSet.getInt(1) > 0
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveMovie(
        movieId: String,
        movie: Movie,
    ) {
        val sql =
            """
            insert into movies (movie_id,title)
            values (?, ?)
            """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setString(1, movieId)
            statement.setString(2, movie.title.value)
            statement.executeUpdate()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveTheater(
        theaterId: String,
        theater: Theater,
    ) {
        val sql =
            """
            insert into theaters (theater_id,open_time,close_time)
            values (?, ?, ?)
            """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setObject(1, theaterId)
            statement.setObject(2, theater.openTime)
            statement.setObject(3, theater.closeTime)
            statement.executeUpdate()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveScreening(
        screeningId: String,
        movieId: String,
        theaterId: String,
        movieTime: MovieTime,
    ) {
        val sql =
            """
            insert into screenings (
            screening_id,
            movie_id,
            theater_id,
            screening_date,
            start_time,
            end_time
            ) values (?, ?, ?, ?, ?, ?)
            """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setObject(1, screeningId)
            statement.setObject(2, movieId)
            statement.setObject(3, theaterId)
            statement.setObject(4, movieTime.date)
            statement.setObject(5, movieTime.startTime)
            statement.setObject(6, movieTime.endTime)
            statement.executeUpdate()
        }
    }

    fun findAllMovieScreenings(): List<MovieScreeningRecord> {
        val sql =
            """
            select
                m.movie_id,
                m.title,
                s.screening_id,
                s.screening_date,
                s.start_time,
                s.end_time
            from screenings s
            join movies m on s.movie_id = m.movie_id
            order by m.title, s.screening_date, s.start_time
            """.trimIndent()

        return connection.prepareStatement(sql).use { statement ->
            statement.executeQuery().use { resultSet ->
                buildList {
                    while (resultSet.next()) {
                        add(
                            MovieScreeningRecord(
                                movieId = resultSet.getString("movie_id"),
                                title = resultSet.getString("title"),
                                screeningId = resultSet.getString("screening_id"),
                                startAt =
                                    LocalDateTime.of(
                                        resultSet.getObject("screening_date", LocalDate::class.java),
                                        resultSet.getObject("start_time", LocalTime::class.java),
                                    ),
                                endAt =
                                    LocalDateTime.of(
                                        resultSet.getObject("screening_date", LocalDate::class.java),
                                        resultSet.getObject("end_time", LocalTime::class.java),
                                    ),
                            ),
                        )
                    }
                }
            }
        }
    }

    fun findByScreeningId(screeningId: String): ScreeningMovie? {
        val sql =
            """
            select
                s.screening_id,
                m.title as movie_title,
                t.open_time,
                t.close_time,
                s.screening_date,
                s.start_time,
                s.end_time
            from screenings s
            join movies m on s.movie_id = m.movie_id
            join theaters t on s.theater_id = t.theater_id
            where s.screening_id = ?
            """.trimIndent()

        return connection.prepareStatement(sql).use { statement ->
            statement.setString(1, screeningId)

            statement.executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    return null
                }
                resultSet.toScreeningMovie()
            }
        }
    }

    fun findByTitleAndDate(
        title: MovieTitle,
        date: LocalDate,
    ): List<ScreeningMovie> {
        val sql =
            """
            select
               s.screening_id,
               m.title as movie_title,
               t.open_time,
               t.close_time,
               s.screening_date,
               s.start_time,
               s.end_time
            from screenings s 
            join movies m on s.movie_id = m.movie_id
            join theaters t on s.theater_id = t.theater_id
            where m.title = ? and s.screening_date = ?
            order by s.start_time
            """.trimIndent()

        return connection.prepareStatement(sql).use { statement ->
            statement.setString(1, title.value)
            statement.setObject(2, date)

            statement.executeQuery().use { resultSet ->
                buildList<ScreeningMovie> {
                    while (resultSet.next()) {
                        add(resultSet.toScreeningMovie())
                    }
                }
            }
        }
    }

    private fun findMovieIdByTitle(title: MovieTitle): String? {
        val sql =
            """
            select movie_id
            from movies
            where title = ?
            limit 1
            """.trimIndent()

        return connection.prepareStatement(sql).use { statement ->
            statement.setString(1, title.value)

            statement.executeQuery().use { resultSet ->
                if (!resultSet.next()) {
                    return null
                }
                resultSet.getString("movie_id")
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun ResultSet.toScreeningMovie(): ScreeningMovie {
        val screeningId = getString("screening_id")

        return ScreeningMovie(
            theater =
                Theater(
                    openTime = getObject("open_time", LocalTime::class.java),
                    closeTime = getObject("close_time", LocalTime::class.java),
                ),
            movie =
                Movie(
                    title = MovieTitle(getString("movie_title")),
                ),
            movieTime =
                MovieTime(
                    date = getObject("screening_date", LocalDate::class.java),
                    startTime = getObject("start_time", LocalTime::class.java),
                    endTime = getObject("end_time", LocalTime::class.java),
                ),
            reservedSeats = findReservedSeats(screeningId),
            screeningId = screeningId,
        )
    }

    private fun findReservedSeats(screeningId: String): List<SeatNumber> {
        val sql =
            """
            select seat_number
            from seats
            where screening_id = ?
            order by seat_number
            """.trimIndent()

        return connection.prepareStatement(sql).use { statement ->
            statement.setString(1, screeningId)

            statement.executeQuery().use { resultSet ->
                buildList {
                    while (resultSet.next()) {
                        add(resultSet.getString("seat_number").toSeatNumber())
                    }
                }
            }
        }
    }

    private fun String.toSeatNumber(): SeatNumber =
        SeatNumber(
            row = Row(first()),
            col = Column(drop(1).toInt()),
        )

    fun hasScreenings(): Boolean {
        val sql =
            """
            select count(*)
            from screenings
            """.trimIndent()

        return connection.prepareStatement(sql).use { statement ->
            statement.executeQuery().use { resultSet ->
                resultSet.next()
                resultSet.getInt(1) > 0
            }
        }
    }
}
