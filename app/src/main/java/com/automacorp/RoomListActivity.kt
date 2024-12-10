package com.automacorp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.dialog.CreateRoomDialog
import com.automacorp.model.*
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import com.automacorp.ui.theme.PurpleGrey80

class RoomListActivity : ComponentActivity() {
    private val viewModel: RoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val roomsState by viewModel.roomsState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.findAll()
            }

            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Rooms", { finish() }) }
                ) { innerPadding ->
                    if (roomsState.error != null) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error loading rooms: ${roomsState.error}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {

                        RoomList(
                            rooms = roomsState.rooms,
                            navigateBack = { finish() },
                            onRoomClick = { roomId -> openRoom(roomId) },
                            onCreateRoom = { showCreateRoomDialog() },
                            onDeleteRoom = { roomId -> deleteRoom(roomId) },
                            onUpdateRoom = { roomId -> openRoomForUpdate(roomId) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun openRoom(roomId: Long) {
        RoomDetailActivity.start(this, roomId)
    }

    private fun openRoomForUpdate(roomId: Long) {
        RoomDetailActivity.start(this, roomId)
    }

    private fun showCreateRoomDialog() {
        val dialog = CreateRoomDialog(this) { roomCommand: RoomCommandDto ->
            viewModel.createRoom(roomCommand)
        }
        dialog.show()
    }

    private fun deleteRoom(roomId: Long) {
        viewModel.deleteRoom(roomId)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RoomListActivity::class.java)
            context.startActivity(intent)
        }
    }
}

@Composable
fun RoomList(
    rooms: List<RoomDto>,
    navigateBack: () -> Unit,
    onRoomClick: (Long) -> Unit,
    onCreateRoom: () -> Unit,
    onDeleteRoom: (Long) -> Unit,
    onUpdateRoom: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRoom) {
                Icon(Icons.Default.Add, "Add Room")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.padding(padding)
        ) {
            items(rooms, key = { it.id }) { room ->
                RoomItem(
                    room = room,
                    modifier = Modifier.clickable { onRoomClick(room.id) },
                    onDelete = onDeleteRoom,
                    onUpdate = onUpdateRoom
                )
            }
        }
    }
}

@Composable
fun RoomItem(
    room: RoomDto,
    modifier: Modifier = Modifier,
    onDelete: (Long) -> Unit = {},
    onUpdate: (Long) -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, PurpleGrey80),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Target temperature: ${room.targetTemperature?.let { String.format("%.1f", it) } ?: "N/A"}°",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${room.currentTemperature?.let { String.format("%.1f", it) } ?: "N/A"}°",
                style = MaterialTheme.typography.headlineLarge,
            )
            IconButton(onClick = { onUpdate(room.id) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Update room"
                )
            }
            IconButton(onClick = { onDelete(room.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete room"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomItemPreview() {
    AutomacorpTheme {
        RoomItem(RoomService.ROOMS[0])
    }
}