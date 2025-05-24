package drazek.jiyt.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import drazek.jiyt.ui.theme.JiytTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JiytTopAppBar(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    TopAppBar(
        title = { Text(text = "Nazwa ekranu") },
        colors = TopAppBarDefaults.topAppBarColors(),
        navigationIcon = {
            IconButton(onClick = {onClick("menu")}) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(onClick = {onClick("settings")}) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null
                )
            }
        }
    )
}

@Preview
@Composable
private fun prevJiytTopAppBar() {
    JiytTheme {
        JiytTopAppBar(onClick = {})
    }
}