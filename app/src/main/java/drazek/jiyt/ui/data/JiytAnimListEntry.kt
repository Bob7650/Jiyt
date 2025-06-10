package drazek.jiyt.ui.data

data class JiytAnimListEntry(
    val fileName: String,
    val frameRate: Double,
    val data: List<List<List<Int>>>
)