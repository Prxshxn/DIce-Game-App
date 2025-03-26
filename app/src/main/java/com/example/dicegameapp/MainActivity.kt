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
 * 3. Tie-Breaking Mechanism:
 *    - Logic: If both players reach the target score in the same turn, they enter a tie-breaking phase
 *    - In tie-breaking mode, each player gets one roll per turn (no rerolls allowed)
 *    - This continues until one player scores higher in a single tie-breaking turn
 *    - Advantages:
 *      - Ensures a definitive winner even in close games
 *      - Creates additional tension and excitement for close matches
 *      - Follows dice game conventions for resolving ties
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
import androidx.compose.runtime.saveable.rememberSaveable
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
                // Remember wins across navigation and device rotation
                var humanWinsTotal by rememberSaveable { mutableStateOf(0) }
                var computerWinsTotal by rememberSaveable { mutableStateOf(0) }
                var targetScore by rememberSaveable { mutableStateOf(101) }
                
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
    var targetScoreText by rememberSaveable { mutableStateOf(targetScore.toString()) }
    
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
    // Use rememberSaveable to preserve state during configuration changes
    var humanDice by rememberSaveable { mutableStateOf(List(5) { 1 }) }
    var computerDice by rememberSaveable { mutableStateOf(List(5) { 1 }) }
    var humanScore by rememberSaveable { mutableStateOf(0) }
    var computerScore by rememberSaveable { mutableStateOf(0) }
    var humanRollsRemaining by rememberSaveable { mutableStateOf(3) }
    var computerRollsRemaining by rememberSaveable { mutableStateOf(3) }
    var selectedDice by rememberSaveable { mutableStateOf(List(5) { false }) }
    var computerSelectedDice by rememberSaveable { mutableStateOf(List(5) { false }) }
    var firstRoll by rememberSaveable { mutableStateOf(true) }
    var gameOver by rememberSaveable { mutableStateOf(false) }
    var humanWins by rememberSaveable { mutableStateOf(false) }
    var tieBreakMode by rememberSaveable { mutableStateOf(false) }
    var tieBreakHumanScore by rememberSaveable { mutableStateOf(0) }
    var tieBreakComputerScore by rememberSaveable { mutableStateOf(0) }
    var tieBreakRound by rememberSaveable { mutableStateOf(0) }

    // Reset selectedDice when starting a new round
    if (humanRollsRemaining == 3 && !tieBreakMode) {
        selectedDice = List(5) { false }
        computerSelectedDice = List(5) { false }
        firstRoll = true
    }

    // Function to handle computer's turn with random strategy
    fun computerTurn() {
        if (computerRollsRemaining <= 0) return
        
        // In tie-break mode, simply roll all dice (no rerolls allowed)
        if (tieBreakMode) {
            computerDice = rollDice()
            computerRollsRemaining = 0
            return
        }
        
        // Normal game logic
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
        val humanReachedTarget = humanScore >= targetScore
        val computerReachedTarget = computerScore >= targetScore
        
        // Check for tie condition
        if (humanReachedTarget && computerReachedTarget) {
            // Enter tie-break mode if not already in it
            if (!tieBreakMode) {
                tieBreakMode = true
                tieBreakRound = 0
                tieBreakHumanScore = 0
                tieBreakComputerScore = 0
                // Reset for tie-break round
                humanRollsRemaining = 1 // Only one roll per tie-break turn
                computerRollsRemaining = 1
                firstRoll = true
            } else {
                // We're already in tie-break mode, compare the tie-break scores
                if (tieBreakHumanScore > tieBreakComputerScore) {
                    gameOver = true
                    humanWins = true
                    onHumanWin()
                    tieBreakMode = false
                } else if (tieBreakComputerScore > tieBreakHumanScore) {
                    gameOver = true
                    humanWins = false
                    onComputerWin()
                    tieBreakMode = false
                } else {
                    // Still tied, continue tie-break
                    tieBreakRound++
                    // Reset for next tie-break round
                    humanRollsRemaining = 1
                    computerRollsRemaining = 1
                    tieBreakHumanScore = 0
                    tieBreakComputerScore = 0
                    firstRoll = true
                }
            }
        } 
        // Normal win conditions if not tied
        else if (humanReachedTarget) {
            gameOver = true
            humanWins = true
            onHumanWin()
        } else if (computerReachedTarget) {
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
                tieBreakMode = false
                tieBreakHumanScore = 0
                tieBreakComputerScore = 0
                tieBreakRound = 0
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
                           "Target to reach: $targetScore\n" +
                           if (tieBreakRound > 0) "Tie-break rounds played: $tieBreakRound" else "",
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
                        tieBreakMode = false
                        tieBreakHumanScore = 0
                        tieBreakComputerScore = 0
                        tieBreakRound = 0
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

        // Tie-break indicator
        if (tieBreakMode) {
            Text(
                text = "TIE-BREAK MODE (Round ${tieBreakRound + 1})",
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Tie-break score: You $tieBreakHumanScore | Computer $tieBreakComputerScore",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Human player's dice
        Text(text = "Your Dice", style = MaterialTheme.typography.headlineSmall)

        // Only allow dice selection after first roll and before all rolls are used
        // But disable selection in tie-break mode
        val canSelectDice = !tieBreakMode && !firstRoll && humanRollsRemaining > 0

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

        // Throw Button (disabled after rolls are used up)
        Button(
            onClick = {
                if (humanRollsRemaining > 0) {
                    if (tieBreakMode) {
                        // In tie-break mode: just roll all dice, no keeping dice
                        humanDice = rollDice()
                        computerTurn() // Computer's tie-break roll
                        
                        // In tie-break mode, update tie-break scores after each throw
                        tieBreakHumanScore = humanDice.sum()
                        tieBreakComputerScore = computerDice.sum()
                        
                        // Use up all rolls in tie-break
                        humanRollsRemaining = 0
                        computerRollsRemaining = 0
                        
                        // Check if tie is broken
                        checkForWinner()
                        
                        // Reset for next tie-break round if still tied
                        if (tieBreakMode && !gameOver) {
                            humanRollsRemaining = 1
                            computerRollsRemaining = 1
                        }
                    } else {
                        // Normal game mode
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
                        
                        if (humanRollsRemaining == 0) {
                            // Round complete
                            useAllComputerRolls() // Use any remaining computer rolls
                            humanScore += humanDice.sum()
                            computerScore += computerDice.sum()
                            checkForWinner() // Check if someone won
                            
                            if (!gameOver && !tieBreakMode) {
                                // Only reset for next turn if game is not over and not in tie-break
                                humanRollsRemaining = 3
                                computerRollsRemaining = 3
                                firstRoll = true
                                selectedDice = List(5) { false }
                                computerSelectedDice = List(5) { false }
                            }
                        }
                    }
                }
            },
            enabled = humanRollsRemaining > 0 && !gameOver,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = if (tieBreakMode) {
                    "Throw (Tie-Break)"
                } else if (firstRoll) {
                    "Throw (First Roll)"
                } else {
                    "Throw (${humanRollsRemaining} rolls left)"
                }
            )
        }

        // Score Button (disabled in tie-break mode)
        Button(
            onClick = {
                if (!tieBreakMode) {
                    useAllComputerRolls() // Use all remaining computer rolls with random strategy
                    humanScore += humanDice.sum()
                    computerScore += computerDice.sum()
                    checkForWinner() // Check if someone won
                    
                    if (!gameOver && !tieBreakMode) {
                        // Only reset for next turn if game is not over and not in tie-break
                        humanRollsRemaining = 3
                        computerRollsRemaining = 3
                        firstRoll = true
                        selectedDice = List(5) { false }
                        computerSelectedDice = List(5) { false }
                    }
                }
            },
            enabled = !gameOver && !tieBreakMode && humanRollsRemaining > 0,
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