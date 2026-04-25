package movie

import movie.controller.MovieController
import movie.infrastructure.db.JdbcReservationRepository
import movie.infrastructure.db.JdbcScreeningRepository
import movie.infrastructure.db.SchemaInitializer
import java.sql.DriverManager
import java.util.Properties

fun main() {
    val properties =
        Properties().apply {
            MovieController::class.java.classLoader
                .getResourceAsStream("application.properties")
                ?.use(::load)
                ?: error("application.properties 파일을 찾을 수 없습니다.")
        }
    val databaseUrl = properties.getProperty("movie.database.url")

    DriverManager.getConnection(databaseUrl, "sa", "").use { connection ->
        val schemaInitializer = SchemaInitializer()
        val screeningRepository = JdbcScreeningRepository(connection)
        val reservationRepository = JdbcReservationRepository(connection)
        val movieFixtures = MovieFixtures()

        schemaInitializer.initialize(connection)
        if (!screeningRepository.hasScreenings()) {
            screeningRepository.saveAll(movieFixtures.screeningMovieList)
        }

        MovieController(
            screeningRepository = screeningRepository,
            reservationRepository = reservationRepository,
            movieFixtures = movieFixtures,
        ).run()
    }
}
