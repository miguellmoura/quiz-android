import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    val questions: List<Question> = listOf(
        Question("De qual país é essa bandeira?", "Brasil", image = "flag_brazil", options = listOf("Brasil", "Argentina", "Alemanha", "França")),
        Question("Qual é a capital da França?", "Paris", image = "eiffel", options = listOf("Paris", "Londres", "Berlim", "Madrid")),
        Question("Qual o animal mais rápido do mundo?", "Falcão-Peregrino", image = "falcao", options = listOf("Falcão-Peregrino", "Guepardo", "Lebre", "Águia")),
        Question("Em que continente está o Egito?", "África", image = "flag_egypt", options = listOf("África", "Ásia", "Europa", "América Latina")),
        Question("Qual é o maior planeta do sistema solar?", "Júpiter", image = "planets", options = listOf("Júpiter", "Saturno", "Urano", "Netuno")),
        Question("Qual é a capital da Suécia?", "Stockholm", image = "suecia", options = listOf("Estocolmo", "Oslo", "Helsinki", "Copenhague")),
    ).shuffled()
}