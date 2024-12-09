package com.automacorp
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.model.RoomViewModel
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.round
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowStatus


class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)
        val viewModel: RoomViewModel by viewModels()

        // Find room by name or ID when activity starts
        param?.let {
            RoomService.findByNameOrId(it)?.let { room ->
                viewModel.findRoom(room.id)
            }
        }

        val onRoomSave: () -> Unit = {
            viewModel.room.value?.let { room ->
                viewModel.updateRoom(room.id, room)
                Toast.makeText(baseContext, "Room ${room.name} was updated", Toast.LENGTH_LONG)
                    .show()
                finish()
            }
        }

        setContent {
            val room by viewModel.room.collectAsState()

            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", { finish() }) },
                    floatingActionButton = {
                        if (room != null) {
                            RoomUpdateButton(onRoomSave)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (room != null) {
                        RoomDetail(
                            room = room!!,
                            onRoomUpdate = { updatedRoom ->
                                viewModel._room.value = updatedRoom
                            },
                            onWindowStatusChange = { windowId, status ->
                                viewModel.updateWindowStatus(windowId, status)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }

    @Composable
    fun NoRoom(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.act_room_none),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun RoomDetail(
        room: RoomDto,
        onRoomUpdate: (RoomDto) -> Unit,
        onWindowStatusChange: (Long, WindowStatus) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.act_room_name),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = room.name,
                onValueChange = { onRoomUpdate(room.copy(name = it)) },
                label = { Text(text = stringResource(R.string.act_room_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Current Temperature
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.act_room_current_temperature),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${room.currentTemperature ?: 0.0}°C",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Temperature
            Text(
                text = stringResource(R.string.act_room_target_temperature),
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = room.targetTemperature?.toFloat() ?: 18.0f,
                onValueChange = {
                    val roundedValue = (round(it * 10.0) / 10.0).toDouble()
                    onRoomUpdate(room.copy(targetTemperature = roundedValue))
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                valueRange = 10f..28f
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${room.targetTemperature ?: 18.0}°C",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )

            // Windows Section
            if (room.windows.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Windows",
                    style = MaterialTheme.typography.titleMedium
                )
                room.windows.forEach { window ->
                    WindowItem(
                        window = window,
                        onStatusChange = { newStatus ->
                            onWindowStatusChange(window.id, newStatus)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun RoomUpdateButton(onClick: () -> Unit) {
        FloatingActionButton(
            onClick = onClick
        ) {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_room_save)
            )
        }
    }

    @Composable
    fun WindowItem(
        window: WindowDto,
        onStatusChange: (WindowStatus) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(window.name)
            Switch(
                checked = window.windowStatus == WindowStatus.OPENED,
                onCheckedChange = { isChecked ->
                    onStatusChange(if (isChecked) WindowStatus.OPENED else WindowStatus.CLOSED)
                }
            )
        }
    }

//@Composable
//fun RoomUpdateButton(onClick: () -> Unit) {
//    ExtendedFloatingActionButton(
////        onClick = { onClick() }
////        icon = {
////            Icon(
////                Icons.Filled.Done,
////                contentDescription = stringResource(R.string.act_room_save),
////            )
////        },
////        text = { Text(text = stringResource(R.string.act_room_save)) }
//    )
//}

//@Preview(showBackground = true)
//@Composable
//fun RoomDetailPreview() {
//    AutomacorpTheme {
//        RoomDetail("Android")
//    }
//}
//}

}