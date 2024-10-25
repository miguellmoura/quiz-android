package com.example.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiz.ui.theme.QuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { MyScreen(navController) }
        composable("questions") { QuestionsActivity() }
        composable("leaderboard") { LeaderboardActivity() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = "Logo do nosso app",
            modifier = Modifier
                .height(200.dp)
                .width(150.dp)
        )
        Text(
            text = "Bem-vindo ao GeoQuiz!",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        Text(
            text = "Teste seus conhecimentos sobre geografia!",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Digite algo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(onClick = {
            Singleton.setUserName(text)
            navController.navigate("questions")
        }) {
            Text("Começar!")
        }

        Button(onClick = {
            Singleton.setUserName(text)
            navController.navigate("leaderboard")
        }) {
            Text("Ranking de melhores jogadores!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    QuizTheme {
        MyScreen(rememberNavController())
    }
}
