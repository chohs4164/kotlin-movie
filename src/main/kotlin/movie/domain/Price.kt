package movie.domain

@JvmInline
value class Price(
    val value: Int,
) {
    init {
        require(value >= 0) { "가격은 음수일 수 없습니다." }
    }

    fun sumPrice(targetPrice: Price): Price = Price(value + targetPrice.value)

    fun minusPrice(targetPrice: Price): Price = Price(value - targetPrice.value)
}
