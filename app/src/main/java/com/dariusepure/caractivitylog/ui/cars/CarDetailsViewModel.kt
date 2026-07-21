package com.dariusepure.caractivitylog.ui.cars


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.cars.CarRepository
import com.dariusepure.caractivitylog.domain.Car
import com.dariusepure.caractivitylog.domain.MileageLog
import com.dariusepure.caractivitylog.domain.VehicleInspection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class CarDetailsUiEvent {
    data class ShowToast(val message: String) : CarDetailsUiEvent()
}

sealed class CarDetailsUiState {
    object Loading : CarDetailsUiState()
    data class Success(
        val car: Car,
        val mileageLogs: List<MileageLog>,
        val inspections: List<VehicleInspection>
    ) : CarDetailsUiState()
    data class Error(val message: String) : CarDetailsUiState()
}

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CarDetailsUiState>(CarDetailsUiState.Loading)
    val state = _state.asStateFlow()
    
    private val _uiEvent = Channel<CarDetailsUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    
    fun loadCarData(carId: String) {
        viewModelScope.launch {
            _state.value = CarDetailsUiState.Loading
            try {
                // Observe car, mileage logs, and inspections reactively
                combine(
                    carRepository.getCarFlow(carId),
                    carRepository.getMileageLogs(carId),
                    carRepository.getInspections(carId)
                ) { car, logs, inspections ->
                    if (car != null) {
                        CarDetailsUiState.Success(car, logs, inspections)
                    } else {
                        CarDetailsUiState.Error("Car not found")
                    }
                }.collect { newState ->
                    _state.value = newState
                }
            } catch (e: Exception) {
                _state.value = CarDetailsUiState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }

    fun addInspection(carId: String, inspection: VehicleInspection) {
        viewModelScope.launch {
            try {
                carRepository.addInspection(carId, inspection)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun updateInspection(carId: String, inspection: VehicleInspection) {
        viewModelScope.launch {
            try {
                carRepository.updateInspection(carId, inspection)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun deleteInspection(carId: String, inspectionId: String) {
        viewModelScope.launch {
            try {
                carRepository.deleteInspection(carId, inspectionId)
            } catch (e: Exception) {
                // handle error
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
