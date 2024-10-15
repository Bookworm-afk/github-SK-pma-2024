package com.example.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeExample()
        }
    }
}

/*
Tato funkce definuje samotnou Composable, což je funkce
v Jetpack Compose, která vykresluje UI.
V tomto případě bude tato funkce obsahovat
veškerou logiku a UI pro tuto jednoduchou aplikaci.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeExample() {

    // Stavy pro jednotlivé textové vstupy
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // Nové pole pro textový vstup (email)
    var score by remember { mutableStateOf("") } // Nové pole pro číselný vstup
    var resultText by remember { mutableStateOf("") }

    // Přidáme Scaffold, abychom mohli přidat TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth()) {
                        Text(
                            "Moje Aplikace",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center) // Zarovnání textu na střed
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.DarkGray  // Nastaví pozadí na černé
                )
            )
        }
    ) { innerPadding ->
        // Zbytek obsahu se vykresluje uvnitř Scaffold s paddingem
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Textová pole pro vstupy
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Jméno") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Příjmení") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = age,
                onValueChange = {
                    // Omezíme vstup na číslice a kontrolujeme, že číslo není větší než 150
                    if (it.all { char -> char.isDigit() } && it.toIntOrNull()?.let { it <= 150 } == true) {
                        age = it
                    }
                },
                label = { Text("Věk (hodnota menší než 151)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Použití číselné klávesnice
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = place,
                onValueChange = { place = it },
                label = { Text("Bydliště") },
                modifier = Modifier.fillMaxWidth()
            )

            // Nové textové pole pro email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            // Nové číselné pole pro hodnocení (rozsah 1-5)
            OutlinedTextField(
                value = score,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.toIntOrNull()?.let { it in 1..5 } == true) {
                        score = it
                    }
                },
                label = { Text("Hodnocení (1-5)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Tlačítka Odeslat a Vymazat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        resultText = "Jmenuji se $name $surname. Je mi $age let, moje bydliště je $place, můj email je $email a mé hodnocení aplikace je $score."
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Odeslat")
                }

                Button(
                    onClick = {
                        name = ""
                        surname = ""
                        age = ""
                        place = ""
                        email = ""
                        score = ""
                        resultText = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0000),  // Barva tlačítka
                        contentColor = Color.White  // Barva textu na tlačítku
                    )
                ) {
                    Text("Vymazat")
                }
            }

            // Výsledek
            if (resultText.isNotEmpty()) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeExample()
}
