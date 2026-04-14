package movie.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class User(
    private val id: Uuid = Uuid.random(),
    private var point: Point = Point(0),
) {
    fun useUserPoint(usePoint: Point) {
        point = point.use(usePoint)
    }

    fun sameUser(target: User): Boolean = id == target.id
}
