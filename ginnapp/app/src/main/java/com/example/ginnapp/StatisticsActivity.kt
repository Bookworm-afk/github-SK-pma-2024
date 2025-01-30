package com.example.ginnapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import com.google.firebase.firestore.FirebaseFirestore

data class UserWalkStats(
    val email: String,
    val walkCount: Int
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
    val context = LocalContext.current

    // Fetch data from Firestore when the screen is loaded
    LaunchedEffect(Unit) {
        fetchUserWalkStatsFromFirestore { stats ->
            userWalkStats = stats
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
                    text = "Dog Walking Statistics",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (userWalkStats.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(userWalkStats) { stat ->
                            UserWalkStatRow(stat) {
                                // Navigate to UserDetailsActivity with the selected user's email
                                val intent = Intent(context, UserDetailsActivity::class.java).apply {
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
                        modifier = Modifier.padding(top = 16.dp)
                    )
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
            Text(text = "Email: ${stat.email}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Number of Walks: ${stat.walkCount}", style = MaterialTheme.typography.bodyLarge)
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
                walkCounts[email] = walkCounts.getOrDefault(email, 0) + 1
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
