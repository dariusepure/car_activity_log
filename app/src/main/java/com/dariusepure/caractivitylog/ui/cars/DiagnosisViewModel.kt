package com.dariusepure.caractivitylog.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.ai.GeminiRepository
import com.dariusepure.caractivitylog.data.cars.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosisViewModel @Inject constructor(
    private val carRepository: CarRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DiagnosisUiState())
    val state = _state.asStateFlow()

    private var carContext = ""
    private var currentCarId: String? = null

    fun loadCarData(carId: String) {
        currentCarId = carId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val car = carRepository.getCar(carId)
            if (car != null) {
                carContext = "Car: ${car.make} ${car.model}, Year: ${car.year}, Engine: ${car.engineSize} ${car.fuelType}"
                _state.update { it.copy(
                    isLoading = false,
                    carName = "${car.make} ${car.model}"
                ) }
            }
            
            // Collect messages from Firestore
            carRepository.getDiagnosisMessages(carId).collect { messages ->
                _state.update { it.copy(
                    messages = if (messages.isEmpty()) {
                        listOf(ChatMessage("Hello! How can I help you with your ${car?.make ?: "car"}?", false))
                    } else {
                        messages
                    }
                ) }
            }
        }
    }

    fun sendMessage(text: String) {
        val carId = currentCarId ?: return
        if (text.isBlank()) return
        
        val userMessage = ChatMessage(text, true)
        
        viewModelScope.launch {
            // Save user message to Firestore
            carRepository.addDiagnosisMessage(carId, userMessage)
            
            _state.update { it.copy(isTyping = true) }

            try {
                val currentMsgs = _state.value.messages
                val result = geminiRepository.getDiagnosisResponse(text, carContext, currentMsgs)
                result.onSuccess { response ->
                    val cleanedResponse = cleanAiResponse(response)
                    val aiMessage = ChatMessage(cleanedResponse, false)
                    carRepository.addDiagnosisMessage(carId, aiMessage)
                    _state.update { it.copy(isTyping = false) }
                }.onFailure { e ->
                    _state.update { it.copy(
                        messages = it.messages + ChatMessage("AI Error: ${e.localizedMessage}", false),
                        isTyping = false
                    ) }
                }
            } catch (t: Throwable) {
                _state.update { it.copy(
                    messages = it.messages + ChatMessage("Critical System Error: ${t.localizedMessage}", false),
                    isTyping = false
                ) }
            }
        }
    }

    fun resetConversation() {
        val carId = currentCarId ?: return
        viewModelScope.launch {
            carRepository.clearDiagnosisMessages(carId)
        }
    }

    private fun cleanAiResponse(text: String): String {
        return text.replace("*", "").replace("#", "")
    }
}
