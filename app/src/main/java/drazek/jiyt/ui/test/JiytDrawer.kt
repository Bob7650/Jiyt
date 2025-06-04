package drazek.jiyt.ui.test

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun JiytModalDrawerSheet(modifier: Modifier = Modifier) {
    ModalDrawerSheet {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Text(
            text = "Jiyt",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) //zmienic na logo ewnetualnie

        //HorizontalDivider()

        NavigationDrawerItem(
            label = { Text(text = "Static images") },
            icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
            selected = false,
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        NavigationDrawerItem(
            label = { Text(text = "Animations") },
            icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
            selected = false,
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        NavigationDrawerItem(
            label = { Text(text = "Connect device") },
            icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
            selected = false,
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}