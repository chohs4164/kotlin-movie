package movie

import movie.controller.MovieController
import movie.infrastructure.db.JdbcReservationRepository
import movie.infrastructure.db.JdbcScreeningRepository
import movie.infrastructure.db.SchemaInitializer
import java.sql.DriverManager

fun main() {
    DriverManager.getConnection(
        "jdbc:h2:file:./build/db/movie;AUTO_SERVER = TRUE",
        "sa",
        "",
    ).use { connection ->
        val movieFixtures = MovieFixtures()
        val screeningRepository = JdbcScreeningRepository(connection)
        val reservationRepository = JdbcReservationRepository(connection)

        SchemaInitializer().initialize(connection)

        // 시드 중복 방지용
        if (!screeningRepository.hasScreenings()) {
            screeningRepository.saveAll(movieFixtures.screeningMovieList)
        }

        MovieController(
            screeningRepository = screeningRepository,
            reservationRepository = reservationRepository,
            movieFixtures = movieFixtures
        ).run()
    }
}
