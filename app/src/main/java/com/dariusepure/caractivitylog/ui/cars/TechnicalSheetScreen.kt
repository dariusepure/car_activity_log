package com.dariusepure.caractivitylog.ui.cars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dariusepure.caractivitylog.ui.common.CarFormatters
import com.dariusepure.caractivitylog.ui.common.ErrorState
import com.dariusepure.caractivitylog.ui.common.LoadingState
import com.dariusepure.caractivitylog.ui.common.SpecificationCard
import com.dariusepure.caractivitylog.domain.displayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicalSheetScreen(
    carId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CarDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(carId) {
        viewModel.loadCarData(carId)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Technical Sheet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val s = state) {
            CarDetailsUiState.Loading -> LoadingState()
            is CarDetailsUiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadCarData(carId) })
            is CarDetailsUiState.Success -> {
                val car = s.car
                val powerText = CarFormatters.formatPower(car)
                val totalValves = car.numberOfCylinders * car.valvesPerCylinder
                val valvesText = if (totalValves > 0) {
                    "$totalValves (${car.valvesPerCylinder} per cylinder)"
                } else "-"
                
                val country = europeanCountries.find { it.code == car.plateCountry }
                val usesMiles = country?.usesMiles == true
                val speedUnit = if (usesMiles) "mph" else "km/h"
                val displayTopSpeed = CarFormatters.fromCanonicalSpeed(car.topSpeed, usesMiles)
                val topSpeedText = if (displayTopSpeed > 0) "${displayTopSpeed.roundToInt()} $speedUnit" else "-"
                
                val tireSizeText = if (car.tireWidth > 0 && car.tireAspectRatio > 0 && car.tireDiameter > 0) {
                    "${car.tireWidth}/${car.tireAspectRatio} R${car.tireDiameter}"
                } else "-"

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = car.displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    TechnicalCategory(title = "Identity & Style") {
                        SpecificationCard(
                            specifications = listOf(
                                "Make" to car.make,
                                "Model" to car.model,
                                "Vehicle Type" to car.vehicleType,
                                "Year" to car.year.takeIf { it != 0 }?.toString().orEmpty(),
                                "Color" to car.color,
                                "License Plate" to car.licensePlate,
                                "Plate Country" to (country?.name ?: car.plateCountry),
                                "VIN" to car.vin,
                                "Manufacturing Country" to car.manufacturingCountry
                            )
                        )
                    }

                    TechnicalCategory(title = "Engine & Performance") {
                        SpecificationCard(
                            specifications = listOf(
                                "Power" to powerText,
                                "Torque" to if (car.torque > 0) "${car.torque} Nm" else "",
                                "Top Speed" to topSpeedText,
                                "Aspiration" to car.aspiration,
                                "Cylinders" to car.numberOfCylinders.takeIf { it != 0 }?.toString().orEmpty(),
                                "Valves" to valvesText,
                                "Engine Size" to if (car.engineSize.isNotBlank()) "${car.engineSize} cc" else "",
                                "Fuel Type" to car.fuelType,
                                "Injection System" to car.fuelSystem,
                                "Engine Code" to car.engineCode,
                                "Engine Layout" to car.engineLayout,
                                "Cylinder Layout" to car.cylinderLayout,
                                "Emission Standard" to car.emissionStandard
                            )
                        )
                    }

                    TechnicalCategory(title = "Transmission & Chassis") {
                        SpecificationCard(
                            specifications = listOf(
                                "Gearbox Type" to car.gearboxType,
                                "Number of Gears" to car.gears,
                                "Drivetrain" to car.drivetrain,
                                "Front Suspension" to car.frontSuspension,
                                "Rear Suspension" to car.rearSuspension,
                                "Front Brakes" to car.frontBrakes,
                                "Rear Brakes" to car.rearBrakes
                            )
                        )
                    }

                    TechnicalCategory(title = "Dimensions & Capacity") {
                        SpecificationCard(
                            specifications = listOf(
                                "Dimensions (LxWxH)" to CarFormatters.formatDimensions(car),
                                "Wheelbase" to if (car.wheelbase > 0) "${car.wheelbase} mm" else "",
                                "Track Width" to if (car.trackWidth > 0) "${car.trackWidth} mm" else "",
                                "Weight" to if (car.weight > 0) "${car.weight} kg" else "",
                                "Seats" to car.numberOfSeats.takeIf { it != 0 }?.toString().orEmpty(),
                                "Doors" to car.numberOfDoors.takeIf { it != 0 }?.toString().orEmpty(),
                                "Boot Space" to if (car.bootSpace > 0) "${car.bootSpace} L" else "",
                                "Fuel Tank" to if (car.fuelTankCapacity > 0) "${car.fuelTankCapacity} L" else "",
                                "Battery Capacity" to if (car.batteryCapacity > 0) "${car.batteryCapacity} kWh" else "",
                                "Tire Size" to tireSizeText
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TechnicalCategory(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}
