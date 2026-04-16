package movie.infrastructure.db

import java.sql.Connection

class SchemaInitializer {
    fun initialize(connection: Connection) {
        val schema =
            javaClass.classLoader
                .getResourceAsStream("schema.sql")
                ?.bufferedReader()
                ?.readText()
                ?: error("schema.sql 파일을 찾을 수 없습니다.")
        val statements =
            schema
                .split(";")
                .map { it.trim() }
                .filter { it.isNotBlank() }

        connection.createStatement().use { statement ->
            statements.forEach { sql ->
                statement.execute(sql)
            }
        }
    }
}