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
                        text = "${car.make} ${car.model}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    TechnicalCategory(title = "Identity") {
                        SpecificationCard(
                            specifications = listOf(
                                "Make" to car.make,
                                "Model" to car.model,
                                "Type" to car.vehicleType,
                                "Year" to car.year.toString(),
                                "Color" to car.color,
                                "VIN" to car.vin
                            )
                        )
                    }

                    TechnicalCategory(title = "Engine & Transmission") {
                        SpecificationCard(
                            specifications = listOf(
                                "Power" to powerText,
                                "Torque" to "${car.torque} Nm",
                                "Top Speed" to topSpeedText,
                                "Aspiration" to car.aspiration.ifBlank { "-" },
                                "Cylinders" to if (car.numberOfCylinders > 0) car.numberOfCylinders.toString() else "-",
                                "Valves" to valvesText,
                                "Engine Code" to car.engineCode,
                                "Engine Layout" to car.engineLayout,
                                "Pollution Standard" to car.emissionStandard,
                                "Engine Size" to if (car.engineSize.isNotBlank()) "${car.engineSize} cc" else "-",
                                "Fuel" to car.fuelType,
                                "Fuel Tank" to if (car.fuelTankCapacity > 0) "${car.fuelTankCapacity} L" else "-",
                                "Battery" to if (car.batteryCapacity > 0) "${car.batteryCapacity} kWh" else "-",
                                "Gearbox" to car.gearboxType,
                                "Drivetrain" to car.drivetrain,
                                "Front Brakes" to car.frontBrakes.ifBlank { "-" },
                                "Rear Brakes" to car.rearBrakes.ifBlank { "-" }
                            )
                        )
                    }

                    TechnicalCategory(title = "Dimensions & Origins") {
                        SpecificationCard(
                            specifications = listOf(
                                "Dimensions" to CarFormatters.formatDimensions(car),
                                "Weight" to if (car.weight > 0) "${car.weight} kg" else "-",
                                "Seats" to if (car.numberOfSeats > 0) car.numberOfSeats.toString() else "-",
                                "Doors" to if (car.numberOfDoors > 0) car.numberOfDoors.toString() else "-",
                                "Boot Space" to if (car.bootSpace > 0) "${car.bootSpace} L" else "-",
                                "Tires" to tireSizeText,
                                "Mfg. Country" to car.manufacturingCountry
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
