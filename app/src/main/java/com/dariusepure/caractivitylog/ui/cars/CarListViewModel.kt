package com.dariusepure.caractivitylog.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.cars.CarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarListViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    val state: StateFlow<CarListUiState> = carRepository.cars
        .map { cars ->
            if (cars.isEmpty()) CarListUiState.Empty
            else CarListUiState.Success(cars)
        }
        .catch { e ->
            emit(CarListUiState.Error(e.localizedMessage ?: "An error occurred"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CarListUiState.Loading
        )

    fun onDeleteCar(carId: String) {
        viewModelScope.launch {
            try {
                carRepository.deleteCar(carId)
            } catch (e: Exception) {
                // Log or handle error if needed
            }
        }
    }
}
