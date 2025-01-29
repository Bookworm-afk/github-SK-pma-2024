package com.example.ginnapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ginnapp.ui.theme.GinnappTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GinnappTheme {
                HomeScreen { message ->
                    showToast(message)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
@Composable
fun HomeScreen(onButtonClick: (String) -> Unit) {
    val context = LocalContext.current // Získejte kontext zde

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Přidání obrázku jako pozadí
                Image(
                    painter = painterResource(id = R.drawable.ginacumak),
                    contentDescription = "Background Image",
                    contentScale = ContentScale.Crop, // Obrázek vyplní celou obrazovku a ořízne přebytek
                    modifier = Modifier.fillMaxSize()
                )

                // Obsah aplikace na vrcholu obrázku
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(context, DogWalkingActivity::class.java)
                            context.startActivity(intent) // Použijte předem získaný context
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(text = "Dog Walking Activity")
                    }

                    Button(
                        onClick = {
                            val intent =
                                Intent(context, StatisticsActivity::class.java)
                            context.startActivity(intent)

                            onButtonClick("Statistics clicked")
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(text = "Statistics")
                    }

                    Button(
                        onClick = {
                            (context as? ComponentActivity)?.finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Exit app")
                    }
                }
            }
        }
    )
}

