package chawan.fame.editerbook.model

data class BlocksItem(
    val depth: Int? = null,
    val data: Data? = null,
    val inlineStyleRanges: List<InlineStyleRangesItem?>? = null,
    val text: String? = null,
    val type: String? = null,
    val key: String? = null,
    val entityRanges: List<Any?>? = null
)
