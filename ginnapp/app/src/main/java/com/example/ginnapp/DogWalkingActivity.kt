package com.example.ginnapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ginnapp.ui.theme.GinnappTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

class DogWalkingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GinnappTheme {
                DogWalkingScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogWalkingScreen() {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val calendarState = rememberUseCaseState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Dog Walking Activity!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { calendarState.show() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Pick a Date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                selectedDate?.let {
                    Text(text = "Selected Date: $it", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedDate != null) {
                            saveWalkingDataToFirestore(selectedDate!!, context)
                        } else {
                            Toast.makeText(context, "Please select a date first.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Start Walking")
                }

                CalendarDialog(
                    state = calendarState,
                    config = CalendarConfig(yearSelection = true),
                    selection = CalendarSelection.Date { date ->
                        selectedDate = date
                    }
                )
            }
        }
    )
}

fun saveWalkingDataToFirestore(selectedDate: LocalDate, context: android.content.Context) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email

    if (userEmail != null) {
        val db = FirebaseFirestore.getInstance()
        val walkingData = hashMapOf(
            "email" to userEmail,
            "date" to selectedDate.toString()
        )

        db.collection("dogWalking")
            .add(walkingData)
            .addOnSuccessListener {
                Toast.makeText(context, "Walking data saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show()
    }
}
