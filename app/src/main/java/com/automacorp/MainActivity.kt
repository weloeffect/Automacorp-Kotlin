package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.service.ApiServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.automacorp.ui.theme.AutomacorpTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val ROOM_PARAM = "com.automacorp.room.attribute"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Action to do when the button is clicked
        val onSayHelloButtonClick: (id: String) -> Unit = { idString ->
            val roomId = idString.toLongOrNull() // Convert the input to Long
            if (roomId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {

                        val response = ApiServices.roomsApiService.findById(roomId).execute()
                        if (response.isSuccessful) {
                            val room: RoomDto? = response.body()
                            room?.let {
                                val intent = Intent(this@MainActivity, RoomActivity::class.java).apply {
                                    putExtra(ROOM_PARAM, it.id)
                                }
                                startActivity(intent)
                            } ?: run {
                                showToast("Room not found")
                            }
                        } else {
                            showToast("Error: ${response.code()} ${response.message()}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast("Error fetching room: ${e.message}")
                    }
                }
            } else {
                showToast("Invalid Room ID")
            }
        }

        setContent {
            AutomacorpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        onClick = onSayHelloButtonClick,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun AppLogo(modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = stringResource(R.string.app_logo_description),
        modifier = modifier.paddingFromBaseline(top = 100.dp).height(80.dp),
    )
}

@Composable
fun Greeting(onClick: (id: String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppLogo(Modifier.padding(top = 32.dp).fillMaxWidth())
        Text(
            stringResource(R.string.act_main_welcome),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        var id by remember { mutableStateOf("") }
        OutlinedTextField(
            id,
            onValueChange = { id = it },
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.act_main_fill_name)) // Update placeholder to indicate ID input
            })

        Button(
            onClick = { onClick(id) },
            modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.act_main_open))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AutomacorpTheme {
//        Greeting("Android")
//    }
//}