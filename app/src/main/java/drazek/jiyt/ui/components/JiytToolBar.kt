package drazek.jiyt.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import drazek.jiyt.R
import drazek.jiyt.ui.theme.JiytTheme

@Composable
fun JiytToolBar(
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onPen: () -> Unit,
    onEraser: () -> Unit,
    onColorPicker: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(painter = painterResource(R.drawable.baseline_undo_24), contentDescription = "Undo")
        }

        IconButton(onClick = {}) {
            Icon(painter = painterResource(R.drawable.baseline_redo_24), contentDescription = "Redo")
        }

        IconButton(onClick = {}) {
            Icon(imageVector = Icons.Filled.Create, contentDescription = "Pen")
        }

        IconButton(onClick = {}) {
            Icon(painter = painterResource(R.drawable.eraser_icon), contentDescription = "Eraser")
        }

        // TODO: implement color picker (need to know how it's received on ESP)
        IconButton(onClick = {}) {
            Icon(painter = painterResource(R.drawable.baseline_color_lens_24), contentDescription = "Color Picker")
        }
    }
}

@Preview
@Composable
private fun PrevToolBar() {
    JiytTheme {
        JiytToolBar({},{},{},{},{})
    }
}