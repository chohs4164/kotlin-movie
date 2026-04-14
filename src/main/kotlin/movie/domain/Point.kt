package movie.domain

@JvmInline
value class Point(
    val value: Int,
) {
    init {
        require(value >= 0) { "포인트는 0 포인트 이상이어야 합니다." }
    }

    fun use(amount: Point): Point {
        require(value >= amount.value) { "보유 포인트가 부족합니다." }
        return Point(value - amount.value)
    }
}
