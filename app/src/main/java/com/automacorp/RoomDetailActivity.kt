package com.automacorp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.automacorp.model.RoomDto
import com.automacorp.model.RoomViewModel
import com.automacorp.ui.theme.AutomacorpTheme
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.ui.text.input.KeyboardType
import com.automacorp.model.WindowStatus

class RoomDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val roomId = intent.getLongExtra("room_id", -1)
        val viewModel: RoomViewModel by viewModels()
        
        if (roomId != -1L) {
            viewModel.findRoom(roomId)
        }

        setContent {
            val room by viewModel.room.collectAsStateWithLifecycle()
            
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room Details", { finish() }) },
                    floatingActionButton = {
                        if (room != null) {
                            FloatingActionButton(
                                onClick = {
                                    room?.let {
                                        viewModel.updateRoom(it.id, it)
                                        Toast.makeText(
                                            this@RoomDetailActivity,
                                            "Room updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Done,
                                    contentDescription = "Save changes"
                                )
                            }
                        }
                    }
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
    companion object {
        fun start(context: Context, roomId: Long) {
            val intent = Intent(context, RoomDetailActivity::class.java).apply {
                putExtra("room_id", roomId)
            }
            context.startActivity(intent)
        }
    }
}

@Composable
fun RoomDetail(
    room: RoomDto,
    onRoomUpdate: (RoomDto) -> Unit,
    onWindowStatusChange: (Long, WindowStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Room: ${room.name}",
            style = MaterialTheme.typography.headlineMedium
        )


        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Current Temperature",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = room.currentTemperature?.toString() ?: "",
                    onValueChange = { newValue ->
                        val temp = newValue.toDoubleOrNull()
                        onRoomUpdate(room.copy(currentTemperature = temp))
                    },
                    label = { Text("Current Temperature (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Target Temperature",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = room.targetTemperature?.toFloat() ?: 20f,
                    onValueChange = { newValue ->
                        onRoomUpdate(room.copy(targetTemperature = newValue.toDouble()))
                    },
                    valueRange = 0f..30f,
                    steps = 60,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${room.targetTemperature?.toString() ?: "20"}°C",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }


        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Windows",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                room.windows.forEach { window ->
                    Text(
                        text = window.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NoRoom(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Room not found",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}