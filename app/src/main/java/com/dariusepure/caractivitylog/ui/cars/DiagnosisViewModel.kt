package com.dariusepure.caractivitylog.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.ai.GeminiRepository
import com.dariusepure.caractivitylog.data.cars.CarRepository
import com.dariusepure.caractivitylog.domain.Car
import com.google.ai.client.generativeai.type.FunctionCallPart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private var currentCar: Car? = null

    fun loadCarData(carId: String) {
        currentCarId = carId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val car = carRepository.getCar(carId)
            currentCar = car
            if (car != null) {
                val mileageLogs = carRepository.getMileageLogs(carId).first()
                val latestKm = mileageLogs.maxByOrNull { it.date }?.km ?: 0.0
                
                carContext = """
                    Car: ${car.make} ${car.model}, Year: ${car.year}, Engine: ${car.engineSize} ${car.fuelType}
                    Specs: Power: ${car.power}${car.powerUnit}, Torque: ${car.torque}Nm, Color: ${car.color}, Gears: ${car.gears}
                    System: ${car.fuelSystem}
                    Current Mileage: $latestKm km
                """.trimIndent()

                _state.update { it.copy(
                    isLoading = false,
                    carName = "${car.make} ${car.model}"
                ) }
            }
            
            // Collect messages from Firestore
            carRepository.getDiagnosisMessages(carId).collect { messages ->
                _state.update { it.copy(
                    messages = if (messages.isEmpty()) {
                        listOf(ChatMessage("Hello! I am your car assistant. How can I help you with your ${car?.make ?: "car"} today?", false))
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
                val response = geminiRepository.getDiagnosisResponse(text, carContext, currentMsgs)
                
                // Handle Function Calls
                val parts = response.candidates.firstOrNull()?.content?.parts ?: emptyList()
                val functionCalls = parts.filterIsInstance<FunctionCallPart>()
                
                var toolConfirmation = ""

                functionCalls.forEach { functionCall ->
                    when (functionCall.name) {
                        "update_car_spec" -> {
                            val field = functionCall.args["field"]
                            val value = functionCall.args["value"]
                            if (field != null && value != null) {
                                handleUpdateCarSpec(field, value)
                                toolConfirmation += "Updated $field to $value. "
                            }
                        }
                        "update_car_mileage" -> {
                            val kmStr = functionCall.args["km"]
                            val km = kmStr?.toDoubleOrNull()
                            if (km != null) {
                                handleUpdateCarMileage(km)
                                toolConfirmation += "Updated mileage to $km km. "
                            }
                        }
                    }
                }

                val aiResponseText = response.text
                if (!aiResponseText.isNullOrBlank()) {
                    val cleanedResponse = cleanAiResponse(aiResponseText)
                    val aiMessage = ChatMessage(cleanedResponse, false)
                    carRepository.addDiagnosisMessage(carId, aiMessage)
                } else if (toolConfirmation.isNotEmpty()) {
                    val aiMessage = ChatMessage(toolConfirmation.trim(), false)
                    carRepository.addDiagnosisMessage(carId, aiMessage)
                }
                
                _state.update { it.copy(isTyping = false) }
                
            } catch (t: Throwable) {
                _state.update { it.copy(
                    messages = it.messages + ChatMessage("AI Error: ${t.localizedMessage}", false),
                    isTyping = false
                ) }
            }
        }
    }

    private suspend fun handleUpdateCarMileage(km: Double) {
        val carId = currentCarId ?: return
        val log = com.dariusepure.caractivitylog.domain.MileageLog(
            km = km,
            date = java.util.Date()
        )
        carRepository.addMileageLog(carId, log)
        // Refresh context to include new mileage
        loadCarData(carId)
    }

    private suspend fun handleUpdateCarSpec(field: String, value: String) {
        val car = currentCar ?: return
        val updatedCar = when (field.lowercase()) {
            "name" -> car.copy(name = value)
            "platecountry" -> car.copy(plateCountry = value.uppercase())
            "make" -> car.copy(make = value)
            "model" -> car.copy(model = value)
            "vin" -> car.copy(vin = value.uppercase())
            "year" -> car.copy(year = value.toIntOrNull() ?: car.year)
            "enginesize" -> car.copy(engineSize = value)
            "fueltype" -> car.copy(fuelType = value)
            "fuelsystem" -> car.copy(fuelSystem = value)
            "color" -> car.copy(color = value.uppercase())
            "power" -> car.copy(power = value.toIntOrNull() ?: car.power)
            "powerunit" -> car.copy(powerUnit = value)
            "torque" -> car.copy(torque = value.toIntOrNull() ?: car.torque)
            "enginecode" -> car.copy(engineCode = value.uppercase())
            "enginelayout" -> car.copy(engineLayout = value)
            "length" -> car.copy(length = value.toIntOrNull() ?: car.length)
            "width" -> car.copy(width = value.toIntOrNull() ?: car.width)
            "height" -> car.copy(height = value.toIntOrNull() ?: car.height)
            "wheelbase" -> car.copy(wheelbase = value.toIntOrNull() ?: car.wheelbase)
            "trackwidth" -> car.copy(trackWidth = value.toIntOrNull() ?: car.trackWidth)
            "emissionstandard" -> car.copy(emissionStandard = value)
            "aspiration" -> car.copy(aspiration = value)
            "fueltankcapacity" -> car.copy(fuelTankCapacity = value.toDoubleOrNull() ?: car.fuelTankCapacity)
            "batterycapacity" -> car.copy(batteryCapacity = value.toDoubleOrNull() ?: car.batteryCapacity)
            "drivetrain" -> car.copy(drivetrain = value)
            "gearboxtype" -> car.copy(gearboxType = value)
            "gears" -> car.copy(gears = value)
            "frontsuspension" -> car.copy(frontSuspension = value)
            "rearsuspension" -> car.copy(rearSuspension = value)
            "frontbrakes" -> car.copy(frontBrakes = value)
            "rearbrakes" -> car.copy(rearBrakes = value)
            "vehicletype" -> car.copy(vehicleType = value)
            "manufacturingcountry" -> car.copy(manufacturingCountry = value)
            "topspeed" -> car.copy(topSpeed = value.toDoubleOrNull() ?: car.topSpeed)
            "weight" -> car.copy(weight = value.toIntOrNull() ?: car.weight)
            "numberofseats" -> car.copy(numberOfSeats = value.toIntOrNull() ?: car.numberOfSeats)
            "numberofcylinders" -> car.copy(numberOfCylinders = value.toIntOrNull() ?: car.numberOfCylinders)
            "valvespercylinder" -> car.copy(valvesPerCylinder = value.toIntOrNull() ?: car.valvesPerCylinder)
            "numberofdoors" -> car.copy(numberOfDoors = value.toIntOrNull() ?: car.numberOfDoors)
            "bootspace" -> car.copy(bootSpace = value.toIntOrNull() ?: car.bootSpace)
            "tirewidth" -> car.copy(tireWidth = value.toIntOrNull() ?: car.tireWidth)
            "tireaspectratio" -> car.copy(tireAspectRatio = value.toIntOrNull() ?: car.tireAspectRatio)
            "tirediameter" -> car.copy(tireDiameter = value.toIntOrNull() ?: car.tireDiameter)
            else -> car
        }
        
        if (updatedCar != car) {
            carRepository.createCar(updatedCar)
            currentCar = updatedCar
            // Refresh context
            loadCarData(car.id)
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
