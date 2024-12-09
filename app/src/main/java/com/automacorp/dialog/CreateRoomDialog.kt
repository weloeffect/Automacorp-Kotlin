package com.automacorp.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import com.automacorp.R
import com.automacorp.model.RoomCommandDto

class CreateRoomDialog(
    context: Context,
    private val onCreateRoom: (RoomCommandDto) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeDialog()
    }

    private fun initializeDialog() {
        try {
            setContentView(R.layout.dialog_create_room)
            setupViews()
        } catch (e: Exception) {
            handleDialogError(e)
        }
    }

    private fun setupViews() {
        // Find views
        val nameInput = findViewById<EditText>(R.id.dialog_room_name_input)
        val tempInput = findViewById<EditText>(R.id.dialog_room_temp_input)
        val createButton = findViewById<Button>(R.id.dialog_create_button)
        val cancelButton = findViewById<Button>(R.id.dialog_cancel_button)

        // Set hints and text from resources
        nameInput.hint = context.getString(R.string.dialog_room_name_hint)
        tempInput.hint = context.getString(R.string.dialog_room_temp_hint)
        createButton.text = context.getString(R.string.dialog_button_create)
        cancelButton.text = context.getString(R.string.dialog_button_cancel)

        // Set click listeners
        createButton.setOnClickListener {
            handleCreateButtonClick(nameInput.text.toString(), tempInput.text.toString())
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun handleCreateButtonClick(name: String, tempString: String) {
        val targetTemp = tempString.toDoubleOrNull()
        
        if (name.isBlank()) {
            showToast(R.string.error_fill_fields)
            return
        }
        
        if (targetTemp == null || targetTemp < 0 || targetTemp > 35) {
            showToast(R.string.error_invalid_temperature)
            return
        }

        val roomCommand = RoomCommandDto(
            name = name,
            targetTemperature = targetTemp,
            currentTemperature = targetTemp
        )
        onCreateRoom(roomCommand)
        dismiss()
    }

    private fun handleDialogError(e: Exception) {
        e.printStackTrace()
        showToast(R.string.error_dialog_init, e.message ?: "Unknown error")
        dismiss()
    }

    private fun showToast(@StringRes messageResId: Int, vararg args: Any) {
        Toast.makeText(
            context,
            context.getString(messageResId, *args),
            Toast.LENGTH_SHORT
        ).show()
    }
} 