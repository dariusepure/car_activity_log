package com.dariusepure.caractivitylog.ui.cars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.cars.CarRepository
import com.dariusepure.caractivitylog.domain.Car
import com.dariusepure.caractivitylog.domain.FuelLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class FuelStats(
    val avgConsumption: Double = 0.0,
    val totalLiters: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalDistance: Double = 0.0
)

data class FuelLogWithConsumption(
    val log: FuelLog,
    val consumption: Double? = null
)

sealed class FuelHistoryUiState {
    object Loading : FuelHistoryUiState()
    data class Success(
        val car: Car,
        val logs: List<FuelLogWithConsumption>,
        val stats: FuelStats
    ) : FuelHistoryUiState()
    data class Error(val message: String) : FuelHistoryUiState()
}

@HiltViewModel
class FuelHistoryViewModel @Inject constructor(
    private val carRepository: CarRepository
) : ViewModel() {

    private val _state = MutableStateFlow<FuelHistoryUiState>(FuelHistoryUiState.Loading)
    val state = _state.asStateFlow()

    fun loadData(carId: String) {
        viewModelScope.launch {
            combine(
                carRepository.getCarFlow(carId),
                carRepository.getFuelLogs(carId)
            ) { car, logs ->
                if (car != null) {
                    val processedLogs = calculateConsumption(logs)
                    val stats = calculateStats(logs)
                    FuelHistoryUiState.Success(car, processedLogs, stats)
                } else {
                    FuelHistoryUiState.Error("Car not found")
                }
            }.collect {
                _state.value = it
            }
        }
    }

    private fun calculateConsumption(logs: List<FuelLog>): List<FuelLogWithConsumption> {
        if (logs.isEmpty()) return emptyList()
        
        val sortedLogs = logs.sortedBy { it.date }
        val result = mutableListOf<FuelLogWithConsumption>()
        
        for (i in sortedLogs.indices) {
            val currentLog = sortedLogs[i]
            var consumption: Double? = null
            
            if (currentLog.isFullTank) {
                // Look back for the previous full tank
                var previousFullTankIndex = -1
                for (j in i - 1 downTo 0) {
                    if (sortedLogs[j].isFullTank) {
                        previousFullTankIndex = j
                        break
                    }
                }
                
                if (previousFullTankIndex != -1) {
                    val dist = currentLog.km - sortedLogs[previousFullTankIndex].km
                    if (dist > 0) {
                        var litersSum = 0.0
                        for (k in previousFullTankIndex + 1..i) {
                            litersSum += sortedLogs[k].liters
                        }
                        consumption = (litersSum / dist) * 100
                    }
                }
            }
            
            result.add(FuelLogWithConsumption(currentLog, consumption))
        }
        
        return result.reversed() // Descending for UI
    }

    private fun calculateStats(logs: List<FuelLog>): FuelStats {
        if (logs.isEmpty()) return FuelStats()
        
        val sorted = logs.sortedBy { it.date }
        val totalLiters = logs.sumOf { it.liters }
        val totalCost = logs.sumOf { it.cost }
        
        // Avg consumption overall (from first full tank to last full tank)
        val fullTanks = sorted.filter { it.isFullTank }
        var avgConsumption = 0.0
        var totalDistance = 0.0
        
        if (fullTanks.size >= 2) {
            val firstFull = fullTanks.first()
            val lastFull = fullTanks.last()
            val dist = lastFull.km - firstFull.km
            if (dist > 0) {
                totalDistance = dist
                val startIndex = sorted.indexOf(firstFull)
                val endIndex = sorted.indexOf(lastFull)
                val litersInBetween = sorted.subList(startIndex + 1, endIndex + 1).sumOf { it.liters }
                avgConsumption = (litersInBetween / dist) * 100
            }
        }
        
        return FuelStats(avgConsumption, totalLiters, totalCost, totalDistance)
    }

    fun addFuelLog(carId: String, km: Double, liters: Double, cost: Double, isFullTank: Boolean, date: Date) {
        viewModelScope.launch {
            carRepository.addFuelLog(carId, FuelLog(km = km, liters = liters, cost = cost, isFullTank = isFullTank, date = date))
        }
    }

    fun deleteFuelLog(carId: String, logId: String) {
        viewModelScope.launch {
            carRepository.deleteFuelLog(carId, logId)
        }
    }
}
