package drazek.jiyt.ui.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import drazek.jiyt.ui.JiytViewModelAnimList
import kotlinx.coroutines.launch

@Composable
fun TestScreen(
    associationOnClick: () -> Unit,
    logOnClick: () -> Unit,
    sendMessageOnClick: () -> Unit,
    selectDevice: (String) -> Unit,
    refreshOnClick: () -> Unit,
    animViewModel: JiytViewModelAnimList,
    modifier: Modifier = Modifier)
{
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            JiytModalDrawerSheet()
        },
    ) {
        Scaffold(
            topBar = {
                TestTopAppBar(onClick = {
                    if (it == "menu"){
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                })
            }
        ) { contentPadding ->
            Column {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            associationOnClick()
                        },
                        modifier = Modifier.padding(contentPadding)
                    ) {
                        Text("Look for device")
                    }

                    Button(
                        onClick = {
                            logOnClick()
                        },
                        modifier = Modifier.padding(contentPadding)
                    ) {
                        Text("Log Connected")
                    }

                    Button(
                        onClick = {
                            sendMessageOnClick()
                        },
                        modifier = Modifier.padding(contentPadding)
                    ) {
                        Text("Send")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Connected devices: ",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { refreshOnClick() }) {
                        Text("Refresh")
                    }
                }

                LazyColumn {
                    if(!animViewModel.bondedNames.value.isEmpty()) {
                        items(animViewModel.bondedNames.value) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(onClick = { selectDevice(it) }) {
                                    Text(
                                        "Connect"
                                    )
                                }
                            }
                        }
                    }else{
                        items(1) {
                            Text(
                                "No paired devices detected",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PrevDeviceRow() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Connected devices: ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {}) {
                Text("Refresh")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Name",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { }) {
                Text(
                    "Connect"
                )
            }
        }
    }
}