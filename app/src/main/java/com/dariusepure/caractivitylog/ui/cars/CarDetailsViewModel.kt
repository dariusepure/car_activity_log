package com.dariusepure.caractivitylog.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.cars.CarRepository
import com.dariusepure.caractivitylog.domain.Car
import com.dariusepure.caractivitylog.domain.MileageLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class CarDetailsUiState {
    object Loading : CarDetailsUiState()
    data class Success(
        val car: Car,
        val mileageLogs: List<MileageLog>
    ) : CarDetailsUiState()
    data class Error(val message: String) : CarDetailsUiState()
}

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CarDetailsUiState>(CarDetailsUiState.Loading)
    val state = _state.asStateFlow()

    fun loadCarData(carId: String) {
        viewModelScope.launch {
            _state.value = CarDetailsUiState.Loading
            try {
                val car = carRepository.getCar(carId)
                if (car != null) {
                    // Observe mileage logs
                    carRepository.getMileageLogs(carId).collect { logs ->
                        _state.value = CarDetailsUiState.Success(car, logs)
                    }
                } else {
                    _state.value = CarDetailsUiState.Error("Car not found")
                }
            } catch (e: Exception) {
                _state.value = CarDetailsUiState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    fun addMileage(carId: String, km: Double, date: Date) {
        viewModelScope.launch {
            try {
                carRepository.addMileageLog(carId, MileageLog(km = km, date = date))
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun updateMileage(carId: String, log: MileageLog) {
        viewModelScope.launch {
            try {
                carRepository.updateMileageLog(carId, log)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun deleteMileage(carId: String, logId: String) {
        viewModelScope.launch {
            try {
                carRepository.deleteMileageLog(carId, logId)
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}
