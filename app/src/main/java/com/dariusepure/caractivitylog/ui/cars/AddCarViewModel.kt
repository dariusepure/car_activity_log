package com.dariusepure.caractivitylog.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.cars.CarRepository
import com.dariusepure.caractivitylog.domain.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddCarViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AddCarState>(AddCarState.Idle)
    val state = _state.asStateFlow()

    fun onAddCar(
        name: String,
        make: String,
        model: String,
        vin: String,
        year: String,
        engineSize: String,
        fuelType: String,
        color: String
    ) {
        if (name.isBlank()) {
            _state.value = AddCarState.Error("Car nickname cannot be empty")
            return
        }

        viewModelScope.launch {
            _state.value = AddCarState.Pending
            try {
                val newCar = Car(
                    name = name,
                    make = make,
                    model = model,
                    vin = vin,
                    year = year.toIntOrNull() ?: 0,
                    engineSize = engineSize,
                    fuelType = fuelType,
                    color = color,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                carRepository.createCar(newCar)
                _state.value = AddCarState.Success
            } catch (e: Exception) {
                _state.value = AddCarState.Error(e.localizedMessage ?: "Failed to add car")
            }
        }
    }

    fun resetState() {
        _state.value = AddCarState.Idle
    }
}
