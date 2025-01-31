package com.example.ginnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.ginnapp.ui.theme.GinnappTheme
import com.google.firebase.firestore.FirebaseFirestore

data class BarChartData(
    val label: String, // Label for the bar (e.g., user email or name)
    val value: Float,  // Value represented by the bar
    val color: Color   // Color of the bar
)

class BarChartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GinnappTheme {
                BarChartScreen()
            }
        }
    }
}

@Composable
fun BarChartScreen() {
    var chartData by remember { mutableStateOf<List<BarChartData>>(emptyList()) }

    // Fetch data from Firestore when the screen is loaded
    LaunchedEffect(Unit) {
        fetchBarChartData { data ->
            chartData = data.take(8) // Limit to 8 users if needed
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
                if (chartData.isNotEmpty()) {
                    HorizontalBarChart(
                        data = chartData,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "No data available.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    )
}

@Composable
fun HorizontalBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    maxBarWidth: Dp = 300.dp // Maximum width for the longest bar
) {
    val maxValue = data.maxOfOrNull { it.value } ?: 1f // Find the maximum value

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Dog Walking Statistics",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        data.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Label on the left side of the bar
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(80.dp) // Fixed width for labels
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Bar with animated width
                val animatedWidth by animateDpAsState(
                    targetValue = (item.value / maxValue) * maxBarWidth
                )
                Box(
                    modifier = Modifier
                        .height(24.dp) // Fixed height for horizontal bars
                        .width(animatedWidth)
                        .background(item.color, shape = MaterialTheme.shapes.small)
                ) {
                    // Place walk count inside the bar if there's enough space
                    if (animatedWidth > 50.dp) {
                        Text(
                            text = item.value.toInt().toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black, // Use contrasting color for visibility inside the bar
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                        )
                    }
                }

                // Place walk count outside the bar if it's too narrow
                if (animatedWidth <= 50.dp) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.value.toInt().toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White // Use white color for visibility outside the bar
                    )
                }
            }
        }
    }
}


fun fetchBarChartData(onResult: (List<BarChartData>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("dogWalking")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val walkCounts = mutableMapOf<String, Int>()

            for (document in querySnapshot.documents) {
                val email = document.getString("email") ?: continue
                walkCounts[email] = walkCounts.getOrDefault(email, 0) + 1
            }

            val chartDataList = walkCounts.entries.mapIndexed { index, entry ->
                BarChartData(
                    label = entry.key,
                    value = entry.value.toFloat(),
                    color = listOf(Color.Blue, Color.Red, Color.Green, Color.Magenta, Color.Yellow, Color.White, Color.Cyan, Color.LightGray)[index % 8] // max 8 walkers
                )
            }

            onResult(chartDataList)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            onResult(emptyList())
        }
}




