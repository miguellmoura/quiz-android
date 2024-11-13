package com.example.quiz.activities

import com.example.quiz.viewmodel.QuizViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiz.R
import com.example.quiz.singleton.Singleton
import com.example.quiz.ui.theme.QuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Singleton.initializeDatabase(this)
        setContent {
            QuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF00BCEB)
                ) {
                    Navigation()
                }
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") { MyScreen(navController) }
        composable("questions") { QuestionsScreen(navController, QuizViewModel()) }
        composable("leaderboard") { LeaderBoardScreen(navController) }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00BCEB)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = "Logo do nosso app",
            modifier = Modifier
                .height(260.dp)
                .width(190.dp)
        )

        Text(
            text = "TESTE SEUS CONHECIMENTOS\nEM GEOGRAFIA!",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Digite seu nome") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color(0xFFE0F7FA), shape = MaterialTheme.shapes.medium)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Singleton.setUserName(text)
                navController.navigate("questions")
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(vertical = 8.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
        ) {
            Text("Jogar", color = Color.White, fontSize = 16.sp)
        }

        Button(
            onClick = {
                navController.navigate("leaderboard")
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(vertical = 8.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
        ) {
            Text("Ranking", color = Color.White, fontSize = 16.sp)
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
