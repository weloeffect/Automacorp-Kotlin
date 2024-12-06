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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.automacorp.model.RoomDto
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round

class RoomViewModel: ViewModel() {
    var room by mutableStateOf <RoomDto?>(null)
}

class RoomActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)
        val viewModel: RoomViewModel by viewModels()
        viewModel.room = RoomService.findByNameOrId(param)
        val onRoomSave: () -> Unit = {
            if(viewModel.room != null) {
                val roomDto: RoomDto = viewModel.room as RoomDto
                RoomService.updateRoom(roomDto.id, roomDto)
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG).show()
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }
        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }
        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", navigateBack) },
//                    floatingActionButton = { RoomUpdateButton(onRoomSave) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (viewModel.room != null) {
                        RoomDetail(
                            model = viewModel,
                            roomDto = viewModel.room!!,  // Non-null assertion since we've checked it's not null
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
    model: RoomViewModel,
        roomDto: RoomDto,
        modifier: Modifier = Modifier,
        onRoomUpdate: (RoomDto) -> Unit = {}
    ) {
        // Use remember to store the room object in a mutable state
        var room by remember { mutableStateOf(roomDto) }

        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.act_room_name),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            // Room Name TextField
            OutlinedTextField(
//                value = room.name,
//                modifier = Modifier.fillMaxWidth(),
//                onValueChange = {
//                    room = room.copy(name = it)
//                    onRoomUpdate(room)
//                },
//                placeholder = { Text(stringResource(R.string.act_room_name)) },
//                label = { Text(stringResource(R.string.act_room_name)) }
                value = model.room?.name ?: "",
                onValueChange = { model.room?.name = it },
                label = { Text(text = stringResource(R.string.act_room_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current Temperature Text (non-editable)
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
                    text = "${room.currentTemperature}°C",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Temperature Slider
            Text(
                text = stringResource(R.string.act_room_target_temperature),
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = room.targetTemperature?.toFloat() ?: 18.0f,
                onValueChange = {
                    val roundedValue = (round(it * 10.0) / 10.0).toDouble()
                    room = room.copy(targetTemperature = roundedValue)
                    onRoomUpdate(room)
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                steps = 0,
                valueRange = 10f..28f
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${round((room.targetTemperature ?: 18.0) * 10) / 10}°C",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
        }
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