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

data class DogWalkingData(
    val email: String = "",
    val date: String = "",
    val length: String = "",
    val pee: Boolean = false,
    val poo: Boolean = false,
    val done: Boolean = false
)

class DogWalkingActivity : ComponentActivity() {
    private var walkingDataListUpdater: ((List<DogWalkingData>) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) { // Initializes the activity and sets up the dog walking screen
        super.onCreate(savedInstanceState)
        setContent {
            GinnappTheme {
                DogWalkingScreen { updater ->
                    walkingDataListUpdater = updater
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogWalkingScreen(onUpdateListCallback: ((List<DogWalkingData>) -> Unit) -> Unit) { //Displays a form for scheduling walks and lists past and future walks.
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val calendarDialogState = rememberUseCaseState()
    var walkingDataList by remember { mutableStateOf<List<DogWalkingData>>(emptyList()) }

    // Stavové proměnné pro vstupy
    var length by remember { mutableStateOf("") }
    var pee by remember { mutableStateOf(false) }
    var poo by remember { mutableStateOf(false) }

    // Callback pro aktualizaci seznamu
    onUpdateListCallback { updatedList ->
        walkingDataList = updatedList
    }

    // Načtení dat z Firestore při spuštění obrazovky
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
                    onClick = { calendarDialogState.show() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Pick a Date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                selectedDate?.let {
                    Text(text = "Selected Date: $it", style = MaterialTheme.typography.bodyLarge)

                    if (it.isBefore(LocalDate.now()) || it.isEqual(LocalDate.now())) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = length,
                            onValueChange = { length = it },
                            label = { Text("Length (e.g., 30 mins)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = pee, onCheckedChange = { pee = it })
                            Text(text = "Pee")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = poo, onCheckedChange = { poo = it })
                            Text(text = "Poo")
                        }
                    } else if (it.isAfter(LocalDate.now())) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = length,
                            onValueChange = { length = it },
                            label = { Text("Approximate Time (e.g., 13:00 h)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedDate == null) {
                            Toast.makeText(context, "Please select a date first.", Toast.LENGTH_SHORT).show()
                        } else if (length.isEmpty()) {
                            Toast.makeText(context, "Please enter the length or time.", Toast.LENGTH_SHORT).show()
                        } else {
                            saveWalkingDataToFirestore(selectedDate!!, context, length, pee, poo)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Save Walk")
                }

                Spacer(modifier=Modifier.height(16.dp))

                // Future Walks Section with Scrollable Box
                if (walkingDataList.any { LocalDate.parse(it.date).isAfter(LocalDate.now()) }) {
                    Text(
                        text="Future Walks",
                        style=MaterialTheme.typography.headlineSmall,
                        modifier=Modifier.padding(bottom=8.dp)
                    )
                    Box(
                        modifier=Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Limit height for scrollability.
                    ) {
                        LazyColumn(contentPadding=PaddingValues(vertical=8.dp)) {
                            items(walkingDataList.filter { LocalDate.parse(it.date).isAfter(LocalDate.now()) }) { data ->
                                FutureWalkRow(data)
                            }
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

                // Past Walks Section with Scrollable Box
                if (walkingDataList.any { !LocalDate.parse(it.date).isAfter(LocalDate.now()) }) {
                    Text(
                        text="Past Walks",
                        style=MaterialTheme.typography.headlineSmall,
                        modifier=Modifier.padding(bottom=8.dp)
                    )
                    Box(
                        modifier=Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Limit height for scrollability.
                    ) {
                        LazyColumn(contentPadding=PaddingValues(vertical=8.dp)) {
                            items(walkingDataList.filter { !LocalDate.parse(it.date).isAfter(LocalDate.now()) }) { data ->
                                WalkingDataRow(data)
                            }
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
fun FutureWalkRow(data: DogWalkingData) {//Displays details of a scheduled future walk
    Card(
        modifier=Modifier.fillMaxWidth().padding(vertical=4.dp),
        elevation=CardDefaults.cardElevation(defaultElevation=4.dp)
    ) {
        Column(modifier=Modifier.padding(8.dp)) {
            Text(text="Date: ${data.date}", style=MaterialTheme.typography.bodyLarge)
            Text(text="Approximate Time/Length: ${data.length}", style=MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun WalkingDataRow(data: DogWalkingData) {//Displays details of a completed or past walk
    Card(
        modifier=Modifier.fillMaxWidth().padding(vertical=4.dp),
        elevation=CardDefaults.cardElevation(defaultElevation=4.dp)
    ) {
        Column(modifier=Modifier.padding(8.dp)) {
            Text(text="Email: ${data.email}", style=MaterialTheme.typography.bodyLarge)
            Text(text="Date: ${data.date}", style=MaterialTheme.typography.bodyLarge)
            Text(text="Length: ${data.length}", style=MaterialTheme.typography.bodyMedium)
            Text(text="Pee: ${if (data.pee) "Yes" else "No"}", style=MaterialTheme.typography.bodyMedium)
            Text(text="Poo: ${if (data.poo) "Yes" else "No"}", style=MaterialTheme.typography.bodyMedium)
            Text(text="Done: ${if (data.done) "Yes" else "No"}", style=MaterialTheme.typography.bodyMedium)
        }
    }
}

fun fetchWalkingDataFromFirestore(onResult: (List<DogWalkingData>) -> Unit) { //Retrieves dog walking data from Firestore for the current user
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

fun saveWalkingDataToFirestore( //Saves scheduled or completed walk data to Firestore
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

        val walkingDataMap =
            hashMapOf("email" to userEmail, "date" to selectedDate.toString(), "done" to false, "length" to length, "pee" to pee, "poo" to poo)

        db.collection("dogWalking")
            .add(walkingDataMap)
            .addOnSuccessListener { Toast.makeText(context, "Future walk saved successfully!", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { e -> Toast.makeText(context, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show() }
    } else {
        Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show()
    }
}
