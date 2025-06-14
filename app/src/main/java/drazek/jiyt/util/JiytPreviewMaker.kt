package drazek.jiyt.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

class JiytPreviewMaker {

    fun makeABitmapFor(grid: List<List<Color>>): Bitmap{
        val outBitmap: Bitmap = createBitmap(16,16,Bitmap.Config.ARGB_8888, hasAlpha = false)

        for(y in grid.indices){
            for(x in grid[y].indices){
                outBitmap[x, y] = grid[y][x].toArgb()
            }
        }

        return outBitmap
    }

    fun getDefaultPreview(): Bitmap{
        val defBitmap = createBitmap(16,16, Bitmap.Config.ARGB_8888, hasAlpha = false)

        defBitmap.setPixels(IntArray(256){android.graphics.Color.GRAY},0,16,0,0,16,16)

        return defBitmap
    }
}