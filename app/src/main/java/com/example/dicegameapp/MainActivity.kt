package com.example.dicegameapp

/**
 * Dice Game App
 *
 * Features implemented:
 * 
 * 1. Configurable Target Score:
 *    - Logic: Players can set their own target score (min 10, default 101) before starting a game
 *    - Advantages:
 *      - Allows for shorter or longer games based on player preference
 *      - Increases replayability by varying game length
 *      - Lets players adjust difficulty by setting lower/higher targets
 *      - Provides flexibility for different play scenarios (quick games vs extended sessions)
 *
 * 2. Win Tracking System:
 *    - Logic: Tracks total wins for both human and computer players during app runtime
 *    - Display format: "H:X/C:Y" where X is human wins and Y is computer wins
 *    - Advantages:
 *      - Provides session-based statistics to track performance
 *      - Creates a sense of competition and achievement
 *      - Allows players to see their win/loss record without permanent storage
 *      - Enhances the game experience by adding a competitive element
 *      - Resets on app restart for fresh start each session
 *
 * Both features enhance the user experience by adding customization and competitive elements
 * while maintaining the core dice game mechanics.
 */

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                // Remember wins across navigation
                var humanWinsTotal by remember { mutableStateOf(0) }
                var computerWinsTotal by remember { mutableStateOf(0) }
                var targetScore by remember { mutableStateOf(101) }
                
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
                                onAboutClicked = { navController.navigate("about") },
                                targetScore = targetScore,
                                onTargetScoreChanged = { targetScore = it }
                            )
                        }
                        composable("about") {
                            AboutScreen(
                                onBackClicked = { navController.popBackStack() }
                            )
                        }
                        composable("game") {
                            GameScreen(
                                onBackClicked = { navController.popBackStack() },
                                humanWinsTotal = humanWinsTotal,
                                computerWinsTotal = computerWinsTotal,
                                onHumanWin = { humanWinsTotal++ },
                                onComputerWin = { computerWinsTotal++ },
                                targetScore = targetScore
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
    onAboutClicked: () -> Unit,
    targetScore: Int,
    onTargetScoreChanged: (Int) -> Unit
) {
    var targetScoreText by remember { mutableStateOf(targetScore.toString()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Target score input field
        Text(
            text = "Set Target Score",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = targetScoreText,
            onValueChange = { newValue ->
                targetScoreText = newValue.filter { it.isDigit() }
                newValue.toIntOrNull()?.let {
                    if (it >= 10) onTargetScoreChanged(it)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            label = { Text("Target Score (min 10)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        
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
        Text(
            text = "Prashan Andradi\n    (20220827)",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 100.dp)
        )
        Text(
            text = "I confirm that I understand what plagiarism is and have read and " +
                    "understood the section on Assessment Offences in the Essential " +
                    "Information for Students. The work that I have submitted is " +
                    "entirely my own. Any work from other authors is duly referenced " +
                    "and acknowledged",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 50.dp)
        )
        Button(onClick = onBackClicked) {
            Text("Back to Home")
        }
    }
}

@Composable
fun GameScreen(
    onBackClicked: () -> Unit,
    humanWinsTotal: Int,
    computerWinsTotal: Int,
    onHumanWin: () -> Unit,
    onComputerWin: () -> Unit,
    targetScore: Int
) {
    var humanDice by remember { mutableStateOf(List(5) { 1 }) }
    var computerDice by remember { mutableStateOf(List(5) { 1 }) }
    var humanScore by remember { mutableStateOf(0) }
    var computerScore by remember { mutableStateOf(0) }
    var humanRollsRemaining by remember { mutableStateOf(3) }
    var computerRollsRemaining by remember { mutableStateOf(3) }
    var selectedDice by remember { mutableStateOf(List(5) { false }) }
    var computerSelectedDice by remember { mutableStateOf(List(5) { false }) }
    var firstRoll by remember { mutableStateOf(true) }
    var gameOver by remember { mutableStateOf(false) }
    var humanWins by remember { mutableStateOf(false) }

    // Reset selectedDice when starting a new round
    if (humanRollsRemaining == 3) {
        selectedDice = List(5) { false }
        computerSelectedDice = List(5) { false }
        firstRoll = true
    }

    // Function to handle computer's turn with random strategy
    fun computerTurn() {
        if (computerRollsRemaining <= 0) return
        
        // First roll always happens
        if (computerRollsRemaining == 3) {
            computerDice = rollDice()
            computerRollsRemaining--
            return
        }
        
        // Randomly decide whether to reroll (70% chance to reroll)
        val willReroll = Random.nextDouble() < 0.7
        
        if (willReroll && computerRollsRemaining > 0) {
            // Randomly decide which dice to keep
            computerSelectedDice = List(5) { Random.nextBoolean() }
            
            // Reroll only unselected dice
            computerDice = computerDice.mapIndexed { index, value ->
                if (computerSelectedDice[index]) value else Random.nextInt(1, 7)
            }
            computerRollsRemaining--
        } else {
            // Computer decides not to reroll
            computerRollsRemaining = 0
        }
    }
    
    // Function to use all remaining computer rolls
    fun useAllComputerRolls() {
        while (computerRollsRemaining > 0) {
            computerTurn()
        }
    }
    
    // Function to check for a winner
    fun checkForWinner() {
        if (humanScore >= targetScore) {
            gameOver = true
            humanWins = true
            onHumanWin()
        } else if (computerScore >= targetScore) {
            gameOver = true
            humanWins = false
            onComputerWin()
        }
    }
    
    // Show game over dialog if the game is over
    if (gameOver) {
        AlertDialog(
            onDismissRequest = { 
                // Reset the game when dialog is dismissed
                gameOver = false
                humanScore = 0
                computerScore = 0
                humanRollsRemaining = 3
                computerRollsRemaining = 3
                firstRoll = true
                selectedDice = List(5) { false }
                computerSelectedDice = List(5) { false }
            },
            title = { 
                Text(
                    text = if (humanWins) "You win!" else "You lose",
                    color = if (humanWins) Color.Green else Color.Red,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            text = { 
                Text(
                    text = "Final score: You $humanScore - Computer $computerScore\n" +
                           "Target to reach: $targetScore",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Reset the game when dialog is dismissed
                        gameOver = false
                        humanScore = 0
                        computerScore = 0
                        humanRollsRemaining = 3
                        computerRollsRemaining = 3
                        firstRoll = true
                        selectedDice = List(5) { false }
                        computerSelectedDice = List(5) { false }
                    }
                ) {
                    Text("Play Again")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Reset the game and go back to home
                        gameOver = false
                        onBackClicked()
                    }
                ) {
                    Text("Back to Menu")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with scores
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Win counts on the left
            Text(
                text = "H:$humanWinsTotal/C:$computerWinsTotal",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
            
            // Current round scores on the right
            Text(
                text = "You: $humanScore | Computer: $computerScore",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Human player's dice
        Text(text = "Your Dice", style = MaterialTheme.typography.headlineSmall)

        // Only allow dice selection after first roll and before all rolls are used
        val canSelectDice = !firstRoll && humanRollsRemaining > 0

        if (canSelectDice) {
            Text(
                text = "Tap dice to keep them for next roll",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        DiceRow(
            diceValues = humanDice,
            selectedDice = selectedDice,
            onDiceSelected = { index ->
                if (canSelectDice) {
                    selectedDice = selectedDice.toMutableList().apply {
                        this[index] = !this[index]
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Computer's dice
        Text(text = "Computer's Dice", style = MaterialTheme.typography.headlineSmall)
        DiceRow(
            diceValues = computerDice,
            selectedDice = computerSelectedDice,
            onDiceSelected = { }  // Computer dice are not selectable by human
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Throw Button (disabled after 3 rolls)
        Button(
            onClick = {
                if (humanRollsRemaining > 0) {
                    if (firstRoll) {
                        // First roll: roll all dice
                        humanDice = rollDice()
                        computerTurn() // First computer roll
                        firstRoll = false
                    } else {
                        // Subsequent rolls: only reroll unselected dice
                        humanDice = humanDice.mapIndexed { index, value ->
                            if (selectedDice[index]) value else Random.nextInt(1, 7)
                        }
                        computerTurn() // Computer's turn with random strategy
                    }
                    humanRollsRemaining--
                }

                if (humanRollsRemaining == 0) {
                    // Round complete
                    useAllComputerRolls() // Use any remaining computer rolls
                    humanScore += humanDice.sum()
                    computerScore += computerDice.sum()
                    checkForWinner() // Check if someone won
                    if (!gameOver) {
                        // Only reset for next turn if game is not over
                        humanRollsRemaining = 3
                        computerRollsRemaining = 3
                        firstRoll = true
                        selectedDice = List(5) { false }
                        computerSelectedDice = List(5) { false }
                    }
                }
            },
            enabled = humanRollsRemaining > 0 && !gameOver,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = if (firstRoll) "Throw (First Roll)" else "Throw (${humanRollsRemaining} rolls left)")
        }

        // Score Button
        Button(
            onClick = {
                useAllComputerRolls() // Use all remaining computer rolls with random strategy
                humanScore += humanDice.sum()
                computerScore += computerDice.sum()
                checkForWinner() // Check if someone won
                if (!gameOver) {
                    // Only reset for next turn if game is not over
                    humanRollsRemaining = 3
                    computerRollsRemaining = 3
                    firstRoll = true
                    selectedDice = List(5) { false }
                    computerSelectedDice = List(5) { false }
                }
            },
            enabled = !gameOver,
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
fun DiceRow(
    diceValues: List<Int>,
    selectedDice: List<Boolean>,
    onDiceSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        diceValues.forEachIndexed { index, value ->
            DiceImage(
                value = value,
                isSelected = selectedDice[index],
                onClick = { onDiceSelected(index) }
            )
        }
    }
}

@Composable
fun DiceImage(
    value: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val imageRes = when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> throw IllegalArgumentException("Invalid dice value")
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Dice $value",
            modifier = Modifier
                .size(64.dp)
                .then(
                    if (isSelected) {
                        Modifier
                            .border(width = 3.dp, color = Color.Green, shape = MaterialTheme.shapes.small)
                            .background(Color.Green.copy(alpha = 0.3f))
                    } else {
                        Modifier
                    }
                )
        )
    }
}

fun rollDice(): List<Int> {
    return List(5) { Random.nextInt(1, 7) } // Generates 5 random dice values (1-6)
}