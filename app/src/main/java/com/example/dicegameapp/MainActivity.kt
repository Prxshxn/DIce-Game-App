

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    HomeScreen(
                        onNewGameClicked = { /* Handle New Game button click */ },
                        onAboutClicked = { /* Handle About button click */ }
                    )
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
        // New Game Button
        Button(
            onClick = onNewGameClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "New Game")
        }

        // About Button
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DiceGameAppTheme {
        HomeScreen(
            onNewGameClicked = { },
            onAboutClicked = { }
        )
    }
}
