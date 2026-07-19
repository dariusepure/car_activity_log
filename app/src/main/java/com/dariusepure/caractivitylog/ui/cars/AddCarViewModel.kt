package com.dariusepure.caractivitylog.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.cars.CarRepository
import com.dariusepure.caractivitylog.domain.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddCarViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AddCarState>(AddCarState.Idle)
    val state = _state.asStateFlow()

    // 1. Am adăugat BUFFERED pentru ca UI-ul să poată prinde evenimentul fără blocaje
    private val _navigationEvent = Channel<Unit>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private var currentCarId: String? = null

    fun loadCar(carId: String) {
        currentCarId = carId
        viewModelScope.launch {
            _state.value = AddCarState.Pending
            try {
                val car = carRepository.getCar(carId)
                if (car != null) {
                    _state.value = AddCarState.Idle
                }
            } catch (e: Exception) {
                _state.value = AddCarState.Error(e.localizedMessage ?: "Failed to load car")
            }
        }
    }

    suspend fun getCarData(carId: String): Car? = carRepository.getCar(carId)

    fun onAddOrUpdateCar(
        name: String, // License Plate
        plateCountry: String,
        make: String,
        model: String,
        vin: String,
        year: String,
        engineSize: String,
        fuelType: String,
        color: String,
        power: String,
        powerUnit: String
    ) {
        if (name.isBlank()) {
            _state.value = AddCarState.Error("License plate cannot be empty")
            return
        }

        // Romanian License Plate Validation
        if (plateCountry == "RO") {
            val roPlateRegex = Regex("^(B|AB|AR|AG|BC|BH|BN|BT|BV|BR|BZ|CS|CL|CJ|CT|CV|DB|DJ|GL|GR|GJ|HR|HD|IL|IS|IF|MM|MH|MS|NT|OT|PH|SM|SJ|SB|SV|TR|TM|TL|VS|VL|VN)\\s?\\d{2,3}\\s?[A-PR-Z]{3}$", RegexOption.IGNORE_CASE)
            if (!name.replace(" ", "").matches(roPlateRegex)) {
                _state.value = AddCarState.Error("Invalid Romanian license plate format (ex: B 123 ABC or CJ 12 ABC)")
                return
            }
        }

        // VIN validation: empty is okay (optional), but if not empty must be 17 chars
        if (vin.isNotBlank() && vin.length != 17) {
            _state.value = AddCarState.Error("VIN must be exactly 17 characters if provided")
            return
        }

        viewModelScope.launch {
            _state.value = AddCarState.Pending
            try {
                val currentCountry = europeanCountries.find { it.code == (currentCarId?.let { carRepository.getCar(it)?.plateCountry } ?: "RO") }
                val newCountry = europeanCountries.find { it.code == plateCountry }
                
                var finalPower = power.toIntOrNull() ?: 0
                // If country changed and units differ, we could convert power too? 
                // But request was specifically for mileage conversion and country selection.

                val car = Car(
                    id = currentCarId ?: "",
                    name = name.uppercase(),
                    plateCountry = plateCountry,
                    make = make,
                    model = model,
                    vin = vin.uppercase(),
                    year = year.toIntOrNull() ?: 0,
                    engineSize = engineSize,
                    fuelType = fuelType,
                    color = color,
                    power = finalPower,
                    powerUnit = powerUnit,
                    updatedAt = Date()
                )

                // Mileage conversion logic if editing
                if (currentCarId != null && currentCountry != null && newCountry != null && currentCountry.usesMiles != newCountry.usesMiles) {
                    val logs = carRepository.getMileageLogs(currentCarId!!).take(1).first()
                    logs.forEach { log ->
                        val convertedKm = if (newCountry.usesMiles) {
                            log.km / 1.609344 // KM to Miles
                        } else {
                            log.km * 1.609344 // Miles to KM
                        }
                        carRepository.updateMileageLog(currentCarId!!, log.copy(km = convertedKm))
                    }
                }

                carRepository.createCar(car)
                _state.value = AddCarState.Success

                // 2. Folosim trySend() care trimite instant semnalul de back și deblochează ecranul
                _navigationEvent.trySend(Unit)

            } catch (e: Exception) {
                _state.value = AddCarState.Error(e.localizedMessage ?: "Failed to save car")
            }
        }
    }

    fun resetState() {
        _state.value = AddCarState.Idle
        currentCarId = null
    }
}