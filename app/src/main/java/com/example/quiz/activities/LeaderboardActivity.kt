package com.example.quiz.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quiz.ui.theme.QuizTheme
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import com.example.quiz.singleton.Singleton
import com.example.quiz.entity.UserScore

class LeaderboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    LeaderBoardScreen(navController)
                }
            }
        }
    }
}
@Composable
fun LeaderBoardScreen(navController: NavController) {
    var topPlayers by remember { mutableStateOf(emptyList<UserScore>()) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Singleton.getDatabase()
            db?.let {
                topPlayers = it.userScoreDao().getAllUserScores().take(10)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ranking",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
            color = Color.White,
            modifier = Modifier.padding(top = 32.dp, bottom = 64.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(24.dp)
        ) {
            Column {
                topPlayers.forEachIndexed { index, player ->
                    val minutes = (player.time / 1000) / 60
                    val seconds = (player.time / 1000) % 60
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${index + 1}. ${player.username} ${player.score} Pontos em ${minutes}m ${seconds}s",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                navController.navigate("home")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Menu Principal")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderBoardScreenPreview() {
    QuizTheme {
        val navController = rememberNavController()
        LeaderBoardScreen(navController)
    }
}