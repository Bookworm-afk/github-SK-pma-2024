package com.example.ginnapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ginnapp.ui.theme.GinnappTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeParseException

data class UserWalkStats(
    val email: String= "",
    val walkCount: Int = 0
)

class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GinnappTheme {
                StatisticsScreen()
            }
        }
    }
}
@Composable
fun StatisticsScreen() {
    var userWalkStats by remember { mutableStateOf<List<UserWalkStats>>(emptyList()) }
    var averageDuration by remember { mutableStateOf(0.0) }
    val context = LocalContext.current

    // Fetch data from Firestore when the screen is loaded
    LaunchedEffect(Unit) {
        fetchUserWalkStatsFromFirestore { stats ->
            userWalkStats = stats
        }
        fetchAverageWalkDurationFromFirestore { avg ->
            averageDuration = avg
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.backgrounddog),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Semi-transparent overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = 0.6f)
                        .background(Color.Black)
                )

                // Content on top of the background and overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Dog Walking Statistics",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Display Average Walk Duration
                    Text(
                        text = "Average Walk Duration: ${String.format("%.2f", averageDuration)} mins",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Button to navigate to Bar Chart screen
                    Button(
                        onClick = {
                            val intent = Intent(context, BarChartActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(text = "View Walk Data Chart")
                    }

                    if (userWalkStats.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(userWalkStats) { stat ->
                                UserWalkStatRow(stat) {
                                    val intent =
                                        Intent(context, UserDetailsActivity::class.java).apply {
                                            putExtra("email", stat.email)
                                        }
                                    context.startActivity(intent)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No walking records available.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    )
}


@Composable
fun UserWalkStatRow(stat: UserWalkStats, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Email: ${stat.email}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Text(text = "Number of Walks: ${stat.walkCount}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        }
    }
}
fun fetchUserWalkStatsFromFirestore(onResult: (List<UserWalkStats>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("dogWalking")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val walkCounts = mutableMapOf<String, Int>()

            // Count walks per email
            for (document in querySnapshot.documents) {
                val email = document.getString("email") ?: continue
                val dateString = document.getString("date")

                // Validate and parse the date string
                if (!dateString.isNullOrBlank()) {
                    try {
                        val date = LocalDate.parse(dateString) // Ensure proper format
                        walkCounts[email] = walkCounts.getOrDefault(email, 0) + 1
                    } catch (e: DateTimeParseException) {
                        e.printStackTrace() // Log parsing error for debugging
                    }
                } else {
                    // Handle missing/empty date field if necessary
                    println("Skipping record with missing or empty date for email: $email")
                }
            }

            // Convert map to list of UserWalkStats objects
            val statsList = walkCounts.map { (email, count) ->
                UserWalkStats(email, count)
            }

            onResult(statsList)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            onResult(emptyList())
        }
}


fun fetchAverageWalkDurationFromFirestore(onResult: (Double) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("dogWalking")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val durations =
                querySnapshot.documents.mapNotNull { it.getString("length")?.toDoubleOrNull() }

            if (durations.isNotEmpty()) {
                val averageDuration =
                    durations.sum() / durations.size // Calculate average duration
                onResult(averageDuration)
            } else {
                onResult(0.0) // No data available
            }
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            onResult(0.0) // Handle failure gracefully
        }
}

