package com.example.dicegameapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dicegameapp.ui.theme.DiceGameAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGameAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                onNewGameClicked = { /* Handle New Game button click */ },
                                onAboutClicked = { navController.navigate("about") }
                            )
                        }
                        composable("about") {
                            AboutScreen(
                                onBackClicked = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onNewGameClicked: () -> Unit,
    onAboutClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNewGameClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "New Game")
        }
        Button(
            onClick = onAboutClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "About")
        }
    }
}

@Composable
fun AboutScreen(
    onBackClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display your name
        Text(
            text = "Prashan Andradi\n    (20220827)",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 100.dp)
        )

        // Display your details
        Text(
            text = "I confirm that I understand what plagiarism is and have read and" +
                    "understood the section on Assessment Offences in the Essential" +
                    "Information for Students. The work that I have submitted is" +
                    "entirely my own. Any work from other authors is duly referenced" +
                    "and acknowledged",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 50.dp)
        )

        // Back button
        Button(onClick = onBackClicked) {
            Text("Back to Home")
        }
    }
}