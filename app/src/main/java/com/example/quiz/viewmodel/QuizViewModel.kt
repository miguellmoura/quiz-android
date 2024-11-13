package com.example.quiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.quiz.activities.Question

class QuizViewModel : ViewModel() {
    val questions: List<Question> = listOf(
        Question("De qual país é essa bandeira?", "Brasil", image = "flag_brazil", options = listOf("Alemanha", "Argentina", "Brasil", "França").shuffled()),
        Question("Qual é a capital da França?", "Paris", image = "eiffel", options = listOf("Madrid", "Londres", "Berlim", "Paris").shuffled()),
        Question("Qual o animal mais rápido do mundo?", "Falcão-Peregrino", image = "falcao", options = listOf("Falcão-Peregrino", "Guepardo", "Lebre", "Águia").shuffled()),
        Question("Em que continente está o Egito?", "África", image = "flag_egypt", options = listOf("África", "Ásia", "Europa", "América Latina").shuffled()),
        Question("Qual é o maior planeta do sistema solar?", "Júpiter", image = "planets", options = listOf("Júpiter", "Saturno", "Urano", "Netuno").shuffled()),
        Question("Qual é a capital da Suécia?", "Stockholm", image = "suecia", options = listOf("Estocolmo", "Oslo", "Helsinki", "Copenhague").shuffled()),
        Question("De qual país é esse hino?", "Malta", image=null, audio="hinoCortado", options = listOf("Madagascar", "Lituânia", "Malta", "Estônia").shuffled()),
    ).shuffled()
}