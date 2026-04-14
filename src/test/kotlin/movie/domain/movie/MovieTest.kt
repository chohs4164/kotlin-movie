package movie.domain.movie

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class MovieTest {
    @Test
    fun `영화 제목이 공백일 경우 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            val movie = Movie(title = MovieTitle(""))
        }
        assertThrows<IllegalArgumentException> {
            val movie = Movie(title = MovieTitle("\t"))
        }
        assertThrows<IllegalArgumentException> {
            val movie = Movie(title = MovieTitle("        "))
        }
        assertThrows<IllegalArgumentException> {
            val movie = Movie(title = MovieTitle("\n"))
        }
    }
}
