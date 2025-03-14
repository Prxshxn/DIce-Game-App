package com.example.dicegameapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dicegameapp.ui.theme.DiceGameAppTheme
import kotlin.random.Random

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
                                onNewGameClicked = { navController.navigate("game") },
                                onAboutClicked = { navController.navigate("about") }
                            )
                        }
                        composable("about") {
                            AboutScreen(
                                onBackClicked = { navController.popBackStack() }
                            )
                        }
                        composable("game") {
                            GameScreen(
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

@Composable
fun GameScreen(
    onBackClicked: () -> Unit
) {
    var humanDice by remember { mutableStateOf(List(5) { 1 }) } // Human player's dice
    var computerDice by remember { mutableStateOf(List(5) { 1 }) } // Computer's dice

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display human player's dice
        Text(text = "Your Dice", style = MaterialTheme.typography.headlineSmall)
        DiceRow(diceValues = humanDice)

        Spacer(modifier = Modifier.height(16.dp))

        // Display computer's dice
        Text(text = "Computer's Dice", style = MaterialTheme.typography.headlineSmall)
        DiceRow(diceValues = computerDice)

        Spacer(modifier = Modifier.height(32.dp))

        // Throw Button
        Button(
            onClick = {
                humanDice = rollDice() // Roll dice for the human player
                computerDice = rollDice() // Roll dice for the computer
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Throw")
        }

        // Score Button
        Button(
            onClick = {
                // Handle scoring logic here
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Score")
        }

        // Back Button
        Button(
            onClick = onBackClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Back to Home")
        }
    }
}

@Composable
fun DiceRow(diceValues: List<Int>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        diceValues.forEach { value ->
            DiceImage(value = value)
        }
    }
}

@Composable
fun DiceImage(value: Int) {
    val imageRes = when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> throw IllegalArgumentException("Invalid dice value")
    }
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Dice $value",
        modifier = Modifier.size(64.dp)
    )
}

fun rollDice(): List<Int> {
    return List(5) { Random.nextInt(1, 7) } // Random values between 1 and 6
}