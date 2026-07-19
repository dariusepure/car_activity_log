package com.dariusepure.caractivitylog.ui.cars

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dariusepure.caractivitylog.data.cars.CarRepository
import com.dariusepure.caractivitylog.domain.Car
import com.dariusepure.caractivitylog.domain.MileageLog
import com.dariusepure.caractivitylog.domain.VehicleInspection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Date
import javax.inject.Inject

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

    fun loadCarData(carId: String) {
        viewModelScope.launch {
            _state.value = CarDetailsUiState.Loading
            try {
                val car = carRepository.getCar(carId)
                if (car != null) {
                    // Observe mileage logs and inspections
                    combine(
                        carRepository.getMileageLogs(carId),
                        carRepository.getInspections(carId)
                    ) { logs, inspections ->
                        CarDetailsUiState.Success(car, logs, inspections)
                    }.collect { newState ->
                        _state.value = newState
                    }
                } else {
                    _state.value = CarDetailsUiState.Error("Car not found")
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

    fun updateProfileImage(carId: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val outputStream = ByteArrayOutputStream()
                
                // Compression: max 1024px, 70% quality
                val scaledBitmap = if (bitmap.width > 1024 || bitmap.height > 1024) {
                    scaleBitmap(bitmap, 1024)
                } else {
                    bitmap
                }
                
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val imageData = outputStream.toByteArray()
                
                carRepository.uploadCarProfileImage(carId, imageData)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}
