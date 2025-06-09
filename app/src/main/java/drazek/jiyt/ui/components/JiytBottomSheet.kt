package drazek.jiyt.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import drazek.jiyt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JiytBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        ListItem(
            headlineContent = { Text("Delete") },
            leadingContent = { Icon(painter = painterResource(R.drawable.baseline_delete_24), contentDescription = null) },
            modifier = Modifier.clickable{
                onDelete()
            }
        )
    }
}