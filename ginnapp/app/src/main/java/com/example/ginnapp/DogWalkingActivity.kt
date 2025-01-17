package com.example.ginnapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.Timestamp

data class DogWalkingData(
    val email: String = "",
    val date: String = Timestamp.now().toString(),
    val length: String = "",
    val pee: Boolean = false,
    val poo: Boolean = false,
    val done: Boolean = false
)

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
    val calendarDialogState = rememberUseCaseState() // Properly initialize UseCaseState
    var walkingDataList by remember { mutableStateOf<List<DogWalkingData>>(emptyList()) }

    // Fetch data from Firestore when the screen is loaded
    LaunchedEffect(Unit) {
        fetchWalkingDataFromFirestore { data ->
            walkingDataList = data
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Dog Walking Activity!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { calendarDialogState.show() }, // Show calendar dialog
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

                Spacer(modifier = Modifier.height(16.dp))

                // Display walking data in a table format below the buttons
                if (walkingDataList.isNotEmpty()) {
                    Text(
                        text = "My Walking Data",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(walkingDataList) { data ->
                            WalkingDataRow(data)
                        }
                    }
                } else {
                    Text(text = "No walking data available.", style = MaterialTheme.typography.bodyLarge)
                }

                // Calendar Dialog for date selection
                CalendarDialog(
                    state = calendarDialogState,
                    config = CalendarConfig(yearSelection = true),
                    selection = CalendarSelection.Date { date ->
                        selectedDate = date
                        Toast.makeText(context, "Selected Date: $date", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    )
}
@Composable
fun WalkingDataRow(data: DogWalkingData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Email: ${data.email}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Date: ${data.date}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Length: ${data.length}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Pee: ${if (data.pee) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Poo: ${if (data.poo) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Done: ${if (data.done) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun fetchWalkingDataFromFirestore(onResult: (List<DogWalkingData>) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email

    if (userEmail != null) {
        val db = FirebaseFirestore.getInstance()
        db.collection("dogWalking")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val dataList =
                    querySnapshot.documents.mapNotNull { document -> document.toObject(DogWalkingData::class.java) }
                onResult(dataList)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onResult(emptyList())
            }
    } else {
        onResult(emptyList())
    }
}

fun saveWalkingDataToFirestore(selectedDate: LocalDate, context: android.content.Context) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email

    if (userEmail != null) {
        val db = FirebaseFirestore.getInstance()
        val walkingData = hashMapOf(
            "email" to userEmail,
            "date" to selectedDate.toString(),
            "length" to "s",
            "pee" to false,
            "poo" to false,
            "done" to false
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