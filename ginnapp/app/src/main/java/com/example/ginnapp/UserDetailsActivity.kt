package com.example.ginnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ginnapp.ui.theme.GinnappTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

data class DogWalkingDetail(
    val date: String = "",
    val length: String = "",
    val pee: Boolean = false,
    val poo: Boolean = false,
    val done: Boolean = false,
    val email: String = "",
    val walkCount: Int = 0
)

class UserDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the email passed from StatisticsActivity
        val email = intent.getStringExtra("email") ?: ""

        setContent {
            GinnappTheme {
                UserDetailsScreen(email)
            }
        }
    }
}

@Composable
fun UserDetailsScreen(email: String) {
    var pastWalks by remember { mutableStateOf<List<DogWalkingDetail>>(emptyList()) }
    var futureWalks by remember { mutableStateOf<List<DogWalkingDetail>>(emptyList()) }

    // Fetch data from Firestore when the screen is loaded
    LaunchedEffect(email) {
        fetchWalkingDetailsForUser(email) { past, future ->
            pastWalks = past
            futureWalks = future
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background and overlay setup (unchanged)
                Image(
                    painter = painterResource(id = R.drawable.backgrounddog),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = 0.6f)
                        .background(Color.Black)
                )

                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Walking Details for $email",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (futureWalks.isNotEmpty()) {
                        Text(
                            text = "Future Walks",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(futureWalks) { detail ->
                                WalkingDetailRow(detail)
                            }
                        }
                    }

                    if (pastWalks.isNotEmpty()) {
                        Text(
                            text = "Past Walks",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(pastWalks) { detail ->
                                WalkingDetailRow(detail)
                            }
                        }
                    }

                    if (futureWalks.isEmpty() && pastWalks.isEmpty()) {
                        Text(
                            text = "No walking details available.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(top=16.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun WalkingDetailRow(detail: DogWalkingDetail) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Date: ${detail.date}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Text(text = "Length: ${detail.length}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            Text(text = "Pee: ${if (detail.pee) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            Text(text = "Poo: ${if (detail.poo) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            Text(text = "Done: ${if (detail.done) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
        }
    }
}

fun fetchWalkingDetailsForUser(email: String, onResult: (List<DogWalkingDetail>, List<DogWalkingDetail>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val currentDate = LocalDate.now()

    db.collection("dogWalking")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val pastWalks = mutableListOf<DogWalkingDetail>()
            val futureWalks = mutableListOf<DogWalkingDetail>()

            querySnapshot.documents.mapNotNull { document ->
                val detail = document.toObject(DogWalkingDetail::class.java)?.copy(
                    date = document.getString("date") ?: ""
                )
                if (detail != null) {
                    val walkDate = LocalDate.parse(detail.date)
                    if (walkDate.isBefore(currentDate)) {
                        pastWalks.add(detail)
                    } else {
                        futureWalks.add(detail)
                    }
                }
            }

            onResult(pastWalks, futureWalks)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            onResult(emptyList(), emptyList())
        }
}
