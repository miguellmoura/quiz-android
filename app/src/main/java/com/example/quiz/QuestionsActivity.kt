package com.example.quiz
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiz.ui.theme.QuizTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuestionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF00BCEB)
                ) {
                    QuestionsNavigation()
                }
            }
        }
    }
}

@Composable
fun QuestionsNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "questions") {
        composable("questions") { QuestionsScreen(navController) }
        composable("leaderboard") { LeaderBoardScreen(navController) }
    }
}

@Composable
fun QuestionsScreen(navController: NavController) {
    val questions = listOf(
        Question("De qual país é essa bandeira?", "Brasil", image = "flag_brazil", options = listOf("Brasil", "Argentina", "Alemanha", "França")),
        Question("Qual é a capital da França?", "Paris", image = null, options = listOf("Paris", "Londres", "Berlim", "Madrid")),
        Question("Qual o animal mais rápido do mundo?", "Falcão-Peregrino", image = null, options = listOf("Falcão-Peregrino", "Guepardo", "Lebre", "Águia")),
        Question("Em que continente está o Egito?", "África", image = "flag_egypt", options = listOf("África", "Ásia", "Europa", "América Latina")),
        Question("Qual é o maior planeta do sistema solar?", "Júpiter", image = "planets", options = listOf("Júpiter", "Saturno", "Urano", "Netuno"))
    )

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(30) }
    var isQuizActive by remember { mutableStateOf(true) }
    var score by remember { mutableStateOf(0) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }
    val startTime = remember { System.currentTimeMillis() }
    var showModal by remember { mutableStateOf(false) }
    var totalTime by remember { mutableStateOf(0L) }

    fun goToNextQuestion() {
        selectedAnswer = ""
        message = ""
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
        } else {
            message = "Quiz finalizado! Sua pontuação: $score"
            isQuizActive = false
            val endTime = System.currentTimeMillis()
            totalTime = endTime - startTime
            showModal = true

            // Salvar Score No Banco de Dados
            val userName = Singleton.getUserName()
            val userScore = UserScore(username = userName ?: "Desconhecido", score = score, time = totalTime)
            val db = Singleton.getDatabase()
            db?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    db.userScoreDao().insertUserScore(userScore)
                }
            }
        }
    }

    fun startTimer() {
        timer?.cancel()
        timeLeft = 30
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                message = "Tempo esgotado!"
                goToNextQuestion()
            }
        }.start()
    }

    LaunchedEffect(currentQuestionIndex) {
        if (isQuizActive) startTimer()
    }

    DisposableEffect(Unit) {
        onDispose {
            timer?.cancel()
        }
    }

    val currentQuestion = questions[currentQuestionIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00BCEB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "TEMPO RESTANTE: $timeLeft",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "PONTUAÇÃO: $score",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = currentQuestion.questionText,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        currentQuestion.image?.let { imageRes ->
            val imageId = when (imageRes) {
                "flag_brazil" -> R.drawable.bandeira_brasil
                "flag_egypt" -> R.drawable.bandeira_egito
                "planets" -> R.drawable.planetas_sistema_solar
                else -> null
            }
            imageId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Imagem da Pergunta",
                    modifier = Modifier
                        .size(210.dp, 150.dp)
                        .padding(8.dp)
                        .border(2.dp, Color(0xFFFFA726), shape = MaterialTheme.shapes.medium)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        currentQuestion.options.forEach { option ->
            Button(
                onClick = {
                    selectedAnswer = option
                    if (option == currentQuestion.correctAnswer) {
                        message = "Correto!"
                        score += 5
                    } else {
                        message = "Incorreto!"
                    }
                    goToNextQuestion()
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
            ) {
                Text(text = option, color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(text = message, color = Color.White, fontSize = 16.sp)
        }
    }


    if (showModal) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text(text = "Quiz Finalizado!") },
            text = {
                Column {
                    Text(text = "Sua pontuação: $score")
                    val minutes = (totalTime / 1000) / 60
                    val seconds = (totalTime / 1000) % 60
                    Text(text = "Tempo total: ${minutes}m ${seconds}s")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showModal = false
                        navController.navigate("leaderboard")
                    }
                ) {
                    Text("Ver Ranking")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showModal = false
                        navController.navigate("home")
                    }
                ) {
                    Text("Menu Principal")
                }
            }
        )
    }
}

data class Question(
    val questionText: String,
    val correctAnswer: String,
    val image: String?,
    val options: List<String>
)

@Preview(showBackground = true)
@Composable
fun QuestionsScreenPreview() {
    QuizTheme {
        QuestionsScreen(navController = rememberNavController())
    }
}

