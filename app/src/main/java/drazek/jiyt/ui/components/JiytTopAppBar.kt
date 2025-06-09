package drazek.jiyt.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import drazek.jiyt.ui.theme.JiytTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JiytTopAppBar(
    title: String,
    canGoBack: Boolean = false,
    onBackClicked: () -> Unit = {},
){

    var isAnimationRunning by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        title = {
            Text(title)
        },
        navigationIcon = {
            if(canGoBack) {
                IconButton(
                    onClick = {
                        isAnimationRunning = true
                        onBackClicked()
                    },
                    enabled = !isAnimationRunning
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
    )
}

@Preview
@Composable
private fun PrevTopBar() {
    JiytTheme {
        JiytTopAppBar(title = "Animations",canGoBack = true)
    }
}