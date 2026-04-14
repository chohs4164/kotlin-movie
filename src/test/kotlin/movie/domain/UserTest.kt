package movie.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class UserTest {
    @Test
    fun `포인트가 같더라도 유저 아이디가 다르다면 다른 유저다`() {
        val user1 =
            User(
                point = Point(1000),
            )
        val user2 =
            User(
                point = Point(1000),
            )

        assertThat(user1.sameUser(user2)).isFalse
    }
}
