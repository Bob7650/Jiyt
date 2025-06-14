package drazek.jiyt.ui.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color

data class JiytAnimListEntry(
    val fileName: String,
    val data: List<List<Color>>,
    val prevImage: Bitmap
)