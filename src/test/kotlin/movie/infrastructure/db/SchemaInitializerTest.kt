package movie.infrastructure.db

import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import java.sql.DriverManager
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SchemaInitializerTest {
    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `schema sql을 실행하면 movies 테이블을 조회할 수 있다`() {
        val connection =
            DriverManager.getConnection(
                "jdbc:h2:mem:${Uuid.random()}",
                "sa",
                "",
            )
        val schemaInitializer = SchemaInitializer()

        assertThatCode {
            schemaInitializer.initialize(connection)
            // 두 번 호출 하여도 안죽을 수 있다는 것을 보여주기 위함
            schemaInitializer.initialize(connection)
            connection.createStatement().use { statement ->
                statement.executeQuery("select * from movies")
            }
        }.doesNotThrowAnyException()
    }
}
