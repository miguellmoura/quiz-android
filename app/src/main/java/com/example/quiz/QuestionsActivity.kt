package com.example.quiz

import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quiz.ui.theme.QuizTheme
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quiz.ui.theme.QuizTheme

import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import com.example.quiz.ui.theme.QuizTheme



import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quiz.ui.theme.QuizTheme

class QuestionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("QuestionsActivity criada nessa porrra", "onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuestionsScreen()
                }
            }
        }
    }
}

@Composable
fun QuestionsScreen() {
    var selectedAnswer by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(30) } // 30 segundos de contagem
    var isQuizActive by remember { mutableStateOf(true) }
    var timer: CountDownTimer? = null

    // Temporizador
    LaunchedEffect(isQuizActive) {
        if (isQuizActive) {
            timer = object : CountDownTimer(30000, 1000) { // 30 segundos
                override fun onTick(millisUntilFinished: Long) {
                    timeLeft = (millisUntilFinished / 1000).toInt()
                }

                override fun onFinish() {
                    isQuizActive = false
                    message = "Tempo esgotado!"
                }
            }.start()
        }

    }

    DisposableEffect(key1 = isQuizActive) {
        onDispose {
            timer?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "De qual país é essa bandeira?")

        // Exibir tempo restante
        Text(text = "Tempo restante: $timeLeft segundos", style = MaterialTheme.typography.bodyLarge)

        // Certifique-se de que a imagem existe
        Image(
            painter = painterResource(id = R.drawable.flag_brazil),
            contentDescription = "Bandeira do Brasil",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de resposta
        listOf("Brasil", "Argentina", "Chile", "Uruguai").forEach { country ->
            TextButton(onClick = {
                selectedAnswer = country
                if (country == "Brasil") {
                    message = "Correto!"
                    isQuizActive = false // Para o quiz ao acertar
                } else {
                    message = "Incorreto, tente novamente!"
                }
            }) {
                Text(text = country)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mensagem de resposta
        if (message.isNotEmpty()) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuestionsScreenPreview() {
    QuizTheme {
        QuestionsScreen()
    }
}

