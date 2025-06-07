package drazek.jiyt.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import drazek.jiyt.R
import drazek.jiyt.ui.theme.JiytTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JiytTopAppBar(
    title: String,
    canGoBack: Boolean = false,
    onBackClicked: () -> Unit = {},
    canBeRemoved: Boolean = false,
    onRemoveClick: () -> Unit = {}
){
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        title = {
            Text(title)
        },
        navigationIcon = {
            if(canGoBack) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            // DELETE BUTTON
            if(canBeRemoved) {
                IconButton(
                    modifier = Modifier
                        .padding(start = 5.dp),
                    onClick = onRemoveClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_delete_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun PrevTopBar() {
    JiytTheme {
        JiytTopAppBar(title = "Animations",canGoBack = true, canBeRemoved = true)
    }
}