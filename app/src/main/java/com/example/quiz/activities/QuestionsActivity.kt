package com.example.quiz.activities

import com.example.quiz.viewmodel.QuizViewModel
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.*
import androidx.compose.ui.platform.LocalContext
import com.example.quiz.R
import com.example.quiz.singleton.Singleton
import com.example.quiz.entity.UserScore

class QuestionsActivity : ComponentActivity() {
    private val quizViewModel: QuizViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF00BCEB)
                ) {
                    QuestionsNavigation(quizViewModel)
                }
            }
        }
    }
}

@Composable
fun QuestionsNavigation(quizViewModel: QuizViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "questions") {
        composable("questions") { QuestionsScreen(navController, quizViewModel) }
        composable("leaderboard") { LeaderBoardScreen(navController) }
    }
}

@Composable
fun QuestionsScreen(navController: NavController, quizViewModel: QuizViewModel) {
    var countdown by remember { mutableStateOf(3) }
    var showCountdown by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        showCountdown = false
    }

    if (showCountdown) {
        CountdownScreen(countdown)
    } else {
        QuizContent(navController, quizViewModel)
    }
}

@Composable
fun CountdownScreen(countdown: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00BCEB)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = countdown > 0,
            enter = scaleIn(initialScale = 0.5f) + fadeIn(),
            exit = scaleOut(targetScale = 0.5f) + fadeOut()
        ) {
            Text(
                text = countdown.toString(),
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,

                )
        }
    }
}

@Composable
fun AudioPlayer(context: Context) {
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.hinocortado) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {
                if (isPlaying) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.start()
                }
                isPlaying = !isPlaying
            }
        ) {
            Text(text = if (isPlaying) "Pausar áudio" else "Tocar áudio")
        }
    }
}

@Composable
fun QuizContent(navController: NavController, quizViewModel: QuizViewModel) {
    val questions = quizViewModel.questions
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var timeLeft by remember { mutableIntStateOf(30) }
    var isQuizActive by remember { mutableStateOf(true) }
    var score by remember { mutableIntStateOf(0) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }
    val startTime = remember { System.currentTimeMillis() }
    var showModal by remember { mutableStateOf(false) }
    var totalTime by remember { mutableLongStateOf(0L) }

    fun goToNextQuestion() {
        selectedAnswer = ""
        message = ""
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
        } else {
            message = "Quiz finalizado! Sua pontuação: $score"
            timer?.cancel()
            isQuizActive = false
            val endTime = System.currentTimeMillis()
            totalTime = endTime - startTime
            showModal = true

            val userName = Singleton.getUserName()
            val userScore =
                UserScore(username = userName ?: "Desconhecido", score = score, time = totalTime)
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

    if (currentQuestion.audio != null){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00BCEB))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AudioPlayer(context = LocalContext.current)
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
                    "eiffel" -> R.drawable.eifel
                    "falcao" -> R.drawable.rapido
                    "suecia" -> R.drawable.suecia
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
                        val baseScore = 5
                        val timeBonus = timeLeft // 5
                        if (option == currentQuestion.correctAnswer) {
                            message = "Correto!"
                            score += baseScore + timeBonus
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
    } else {
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
                    "eiffel" -> R.drawable.eifel
                    "falcao" -> R.drawable.rapido
                    "suecia" -> R.drawable.suecia
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
                        val baseScore = 5
                        val timeBonus = timeLeft // 5
                        if (option == currentQuestion.correctAnswer) {
                            message = "Correto!"
                            score += baseScore + timeBonus
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                showModal = false
                                navController.navigate("leaderboard")
                            }
                        ) {
                            Text("Ver Ranking")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showModal = false
                                navController.navigate("home")
                            }
                        ) {
                            Text("Menu Principal")
                        }
                    }
                }
            )
        }
    }


}

data class Question(
    val questionText: String,
    val correctAnswer: String,
    val image: String?,
    val audio: String? = null,
    val options: List<String>
)

@Preview(showBackground = true)
@Composable
fun QuestionsScreenPreview() {
    QuizTheme {
        QuestionsScreen(navController = rememberNavController(), quizViewModel = QuizViewModel())
    }
}

