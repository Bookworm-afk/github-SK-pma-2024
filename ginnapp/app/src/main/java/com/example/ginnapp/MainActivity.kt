package com.example.ginnapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ginnapp.ui.theme.GinnappTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    // Initialize FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()
        setContent {
            GinnappTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    loginWithFirebase(email, password)
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegisterClick = { email, password ->
                                    registerWithFirebase(email, password)
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loginWithFirebase(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login success
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    println("User logged in: ${auth.currentUser?.email}")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    println("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    private fun registerWithFirebase(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    println("User registered: ${auth.currentUser?.email}")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Registration failed
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    println("Registration failed: ${task.exception?.message}")
                }
            }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text(text = "Don't have an account? Register here")
        }
    }
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: (String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onRegisterClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text(text = "Already have an account? Login here")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    GinnappTheme {
        LoginScreen(onLoginClick = { _, _ -> }, onNavigateToRegister = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    GinnappTheme {
        RegisterScreen(onRegisterClick = { _, _ -> }, onNavigateBack = {})
    }
}
