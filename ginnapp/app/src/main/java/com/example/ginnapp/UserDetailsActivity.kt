package com.example.ginnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ginnapp.ui.theme.GinnappTheme
import com.google.firebase.firestore.FirebaseFirestore

data class DogWalkingDetail(
    val date: String = "",
    val length: String = "",
    val pee: Boolean = false,
    val poo: Boolean = false,
    val done: Boolean = false
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
    var walkingDetails by remember { mutableStateOf<List<DogWalkingDetail>>(emptyList()) }

    // Fetch data from Firestore when the screen is loaded
    LaunchedEffect(email) {
        fetchWalkingDetailsForUser(email) { details ->
            walkingDetails = details
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
                    text = "Walking Details for $email",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (walkingDetails.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(walkingDetails) { detail ->
                            WalkingDetailRow(detail)
                        }
                    }
                } else {
                    Text(
                        text = "No walking details available.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
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
            Text(text = "Date: ${detail.date}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Length: ${detail.length}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Pee: ${if (detail.pee) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Poo: ${if (detail.poo) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Done: ${if (detail.done) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun fetchWalkingDetailsForUser(email: String, onResult: (List<DogWalkingDetail>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("dogWalking")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { querySnapshot ->
            val detailsList =
                querySnapshot.documents.mapNotNull { document ->
                    document.toObject(DogWalkingDetail::class.java)?.copy(date=document.getString("date") ?: "")
                }

            onResult(detailsList)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            onResult(emptyList())
        }
}
