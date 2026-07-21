package com.dariusepure.caractivitylog.ui.cars

import com.dariusepure.caractivitylog.data.ai.GeminiRepository
import com.dariusepure.caractivitylog.domain.ScannedCarData
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.cars.CarRepository
import com.dariusepure.caractivitylog.data.auth.AuthRepository
import com.dariusepure.caractivitylog.domain.Car
import com.dariusepure.caractivitylog.ui.common.CarFormatters
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
    private val carRepository: CarRepository,
    private val authRepository: AuthRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AddCarState>(AddCarState.Idle)
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<Unit>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _scannedDataEvent = Channel<ScannedCarData>(Channel.BUFFERED)
    val scannedDataEvent = _scannedDataEvent.receiveAsFlow()

    private val _logoutEvent = Channel<Unit>(Channel.BUFFERED)
    val logoutEvent = _logoutEvent.receiveAsFlow()

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

    fun scanImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.value = AddCarState.Scanning
            geminiRepository.scanRegistrationCertificate(bitmap)
                .onSuccess { data ->
                    _state.value = AddCarState.Idle
                    _scannedDataEvent.trySend(data)
                }
                .onFailure { e ->
                    _state.value = AddCarState.Error(e.localizedMessage ?: "AI Scan failed")
                }
        }
    }

    fun scanDocument(uri: Uri, mimeType: String) {
        viewModelScope.launch {
            _state.value = AddCarState.Scanning
            geminiRepository.scanDocument(uri, mimeType)
                .onSuccess { data ->
                    _state.value = AddCarState.Idle
                    _scannedDataEvent.trySend(data)
                }
                .onFailure { e ->
                    _state.value = AddCarState.Error(e.localizedMessage ?: "AI Scan failed")
                }
        }
    }

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
        powerUnit: String,
        torque: String,
        engineCode: String,
        engineLayout: String,
        emissionStandard: String,
        length: String,
        width: String,
        height: String,
        wheelbase: String,
        trackWidth: String,
        fuelTankCapacity: String,
        batteryCapacity: String,
        drivetrain: String,
        gearboxType: String,
        gears: String,
        aspiration: String,
        frontBrakes: String,
        rearBrakes: String,
        vehicleType: String,
        manufacturingCountry: String,
        topSpeed: String,
        weight: String,
        numberOfSeats: String,
        numberOfCylinders: String,
        valvesPerCylinder: String,
        numberOfDoors: String,
        bootSpace: String,
        tireWidth: String,
        tireAspectRatio: String,
        tireDiameter: String
    ) {
        if (make.isBlank() || model.isBlank()) {
            _state.value = AddCarState.Error("Make and Model are required")
            return
        }

        // License Plate Validation based on selected country
        val country = europeanCountries.find { it.code == plateCountry }
        if (name.isNotBlank() && country?.plateRegex != null) {
            val regex = Regex(country.plateRegex, RegexOption.IGNORE_CASE)
            if (!name.replace(" ", "").replace("-", "").matches(regex)) {
                _state.value = AddCarState.Error("Invalid ${country.name} license plate format (ex: ${country.plateHint})")
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
                val country = europeanCountries.find { it.code == plateCountry }
                val usesMiles = country?.usesMiles == true
                
                val inputTopSpeed = topSpeed.toDoubleOrNull() ?: 0.0
                val canonicalTopSpeed = CarFormatters.toCanonicalSpeed(inputTopSpeed, usesMiles)

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
                    torque = torque.toIntOrNull() ?: 0,
                    engineCode = engineCode,
                    engineLayout = engineLayout,
                    emissionStandard = emissionStandard,
                    aspiration = aspiration,
                    length = length.toIntOrNull() ?: 0,
                    width = width.toIntOrNull() ?: 0,
                    height = height.toIntOrNull() ?: 0,
                    wheelbase = wheelbase.toIntOrNull() ?: 0,
                    trackWidth = trackWidth.toIntOrNull() ?: 0,
                    fuelTankCapacity = fuelTankCapacity.toDoubleOrNull() ?: 0.0,
                    batteryCapacity = batteryCapacity.toDoubleOrNull() ?: 0.0,
                    drivetrain = drivetrain,
                    gearboxType = gearboxType,
                    gears = gears,
                    frontBrakes = frontBrakes,
                    rearBrakes = rearBrakes,
                    vehicleType = vehicleType,
                    manufacturingCountry = manufacturingCountry,
                    topSpeed = canonicalTopSpeed,
                    weight = weight.toIntOrNull() ?: 0,
                    numberOfSeats = numberOfSeats.toIntOrNull() ?: 0,
                    numberOfCylinders = numberOfCylinders.toIntOrNull() ?: 0,
                    valvesPerCylinder = valvesPerCylinder.toIntOrNull() ?: 0,
                    numberOfDoors = numberOfDoors.toIntOrNull() ?: 0,
                    bootSpace = bootSpace.toIntOrNull() ?: 0,
                    tireWidth = tireWidth.toIntOrNull() ?: 0,
                    tireAspectRatio = tireAspectRatio.toIntOrNull() ?: 0,
                    tireDiameter = tireDiameter.toIntOrNull() ?: 0,
                    updatedAt = Date()
                )

                carRepository.createCar(car)
                _state.value = AddCarState.Success

                // 2. Folosim trySend() care trimite instant semnalul de back și deblochează ecranul
                _navigationEvent.trySend(Unit)

            } catch (e: Exception) {
                _state.value = AddCarState.Error(e.localizedMessage ?: "Failed to save car")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _logoutEvent.trySend(Unit)
            } catch (e: Exception) {
                _state.value = AddCarState.Error(e.localizedMessage ?: "Failed to sign out")
            }
        }
    }

    fun resetState() {
        _state.value = AddCarState.Idle
        currentCarId = null
    }
}
