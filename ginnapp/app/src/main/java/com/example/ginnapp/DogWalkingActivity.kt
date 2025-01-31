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
import androidx.compose.ui.graphics.Color
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
    private var walkingDataListUpdater: ((List<DogWalkingData>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GinnappTheme {
                DogWalkingScreen { updater ->
                    walkingDataListUpdater = updater
                }
            }
        }
    }

    fun updateWalkingDataList(data: List<DogWalkingData>) {
        walkingDataListUpdater?.invoke(data)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogWalkingScreen(onUpdateListCallback: ((List<DogWalkingData>) -> Unit) -> Unit) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val calendarDialogState = rememberUseCaseState()
    var walkingDataList by remember { mutableStateOf<List<DogWalkingData>>(emptyList()) }

    // State variables for pee, poo, and length inputs
    var length by remember { mutableStateOf("") }
    var pee by remember { mutableStateOf(false) }
    var poo by remember { mutableStateOf(false) }

    // Provide a callback for updating the list
    onUpdateListCallback { updatedList ->
        walkingDataList = updatedList
    }

    // Fetch data from Firestore when the screen is loaded
    LaunchedEffect(Unit) {
        fetchWalkingDataFromFirestore { data ->
            walkingDataList = data
        }
    }

    // Split data into future and past walks
    val pastWalks = walkingDataList.filter {
        LocalDate.parse(it.date).isBefore(LocalDate.now()) || LocalDate.parse(it.date).isEqual(LocalDate.now())
    }
    val futureWalks = walkingDataList.filter {
        LocalDate.parse(it.date).isAfter(LocalDate.now())
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
                    onClick = { calendarDialogState.show() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Pick a Date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                selectedDate?.let {
                    Text(text = "Selected Date: $it", style = MaterialTheme.typography.bodyLarge)

                    if (!it.isAfter(LocalDate.now())) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = length,
                            onValueChange = { length = it },
                            label = { Text("Length (e.g., 30 mins)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = pee,
                                onCheckedChange = { pee = it }
                            )
                            Text(text = "Pee")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = poo,
                                onCheckedChange = { poo = it }
                            )
                            Text(text = "Poo")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedDate != null) {
                            saveWalkingDataToFirestore(selectedDate!!, context, length, pee, poo)
                        } else {
                            Toast.makeText(context, "Please select a date first.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Start Walking")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Future Walks
                if (futureWalks.isNotEmpty()) {
                    Text(
                        text = "Future Walks",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom=8.dp)
                    )
                    LazyColumn(
                        modifier=Modifier.fillMaxWidth(),
                        contentPadding=PaddingValues(vertical=8.dp)
                    ) {
                        items(futureWalks) { data ->
                            FutureWalkRow(data)
                        }
                    }
                } else {
                    Text(
                        text="No future walks scheduled.",
                        style=MaterialTheme.typography.bodyLarge,
                        modifier=Modifier.padding(top=8.dp)
                    )
                }

                Spacer(modifier=Modifier.height(16.dp))

                // Display Past Walks
                if (pastWalks.isNotEmpty()) {
                    Text(
                        text="Past Walks",
                        style=MaterialTheme.typography.headlineSmall,
                        modifier=Modifier.padding(bottom=8.dp)
                    )
                    LazyColumn(
                        modifier=Modifier.fillMaxWidth(),
                        contentPadding=PaddingValues(vertical=8.dp)
                    ) {
                        items(pastWalks) { data ->
                            WalkingDataRow(data)
                        }
                    }
                } else {
                    Text(
                        text="No past walks available.",
                        style=MaterialTheme.typography.bodyLarge,
                        modifier=Modifier.padding(top=8.dp)
                    )
                }

                CalendarDialog(
                    state=calendarDialogState,
                    config=CalendarConfig(yearSelection=true),
                    selection=CalendarSelection.Date { date ->
                        selectedDate=date
                        Toast.makeText(context, "Selected Date: $date", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    )
}

@Composable
fun FutureWalkRow(data: DogWalkingData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Date: ${data.date}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black // Adjust color as needed for your theme.
            )
        }
    }
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
fun saveWalkingDataToFirestore(
    selectedDate: LocalDate,
    context: android.content.Context,
    length: String,
    pee: Boolean,
    poo: Boolean
) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email

    if (userEmail != null) {
        val db = FirebaseFirestore.getInstance()

        // Determine if the walk is in the past or today
        val isPastWalkOrToday = !selectedDate.isAfter(LocalDate.now())

        val walkingData = hashMapOf(
            "email" to userEmail,
            "date" to selectedDate.toString(),
            "done" to isPastWalkOrToday, // Mark as done if it's today or in the past
            "length" to if (isPastWalkOrToday) length else "",
            "pee" to if (isPastWalkOrToday) pee else false,
            "poo" to if (isPastWalkOrToday) poo else false
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
