package drazek.jiyt.ui.data

// jsonData is here for passing data when navigating
data class JiytAnimListEntry(
    val fileName: String,
    val frameRate: Double,
    val data: List<List<List<Int>>>
)