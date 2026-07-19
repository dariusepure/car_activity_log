package com.dariusepure.caractivitylog.ui.cars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(
    carId: String? = null,
    onCarSaved: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddCarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") } // License Plate
    var selectedCountry by remember { mutableStateOf(europeanCountries.find { it.code == "RO" } ?: europeanCountries[0]) }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var engineSize by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("Petrol") }
    var color by remember { mutableStateOf("") }
    var power by remember { mutableStateOf("") }
    var powerUnit by remember { mutableStateOf("hp") }

    var countryExpanded by remember { mutableStateOf(false) }
    var fuelTypeExpanded by remember { mutableStateOf(false) }
    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "Hybrid", "LPG")

    var powerUnitExpanded by remember { mutableStateOf(false) }
    val powerUnits = listOf("hp", "kw")

    LaunchedEffect(carId) {
        if (carId != null) {
            viewModel.loadCar(carId)
            val car = viewModel.getCarData(carId)
            if (car != null) {
                name = car.name
                selectedCountry = europeanCountries.find { it.code == car.plateCountry } ?: europeanCountries[0]
                make = car.make
                model = car.model
                vin = car.vin
                year = car.year.takeIf { it != 0 }?.toString() ?: ""
                engineSize = car.engineSize
                fuelType = car.fuelType.ifBlank { "Petrol" }
                color = car.color
                power = car.power.takeIf { it != 0 }?.toString() ?: ""
                powerUnit = car.powerUnit.ifBlank { "hp" }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onCarSaved()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (carId == null) "Add New Car" else "Edit Car") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            (state as? AddCarState.Error)?.let { error ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = error.message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(90.dp)) {
                    OutlinedTextField(
                        value = selectedCountry.flag,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Country") },
                        modifier = Modifier.clickable { countryExpanded = true },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, null, Modifier.clickable { countryExpanded = true })
                        }
                    )
                    // Overlay to capture clicks
                    Box(modifier = Modifier.matchParentSize().clickable { countryExpanded = true })
                    
                    DropdownMenu(
                        expanded = countryExpanded,
                        onDismissRequest = { countryExpanded = false },
                        modifier = Modifier.sizeIn(maxHeight = 300.dp)
                    ) {
                        europeanCountries.forEach { country ->
                            DropdownMenuItem(
                                text = { Text("${country.flag} ${country.name}") },
                                onClick = {
                                    selectedCountry = country
                                    countryExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.uppercase() },
                    label = { Text("License Plate") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = state !is AddCarState.Pending,
                    placeholder = { 
                        if (selectedCountry.code == "RO") Text("B 123 ABC") 
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = make,
                onValueChange = { make = it },
                label = { Text("Make (e.g. BMW)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model (e.g. X5)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = year,
                onValueChange = { if (it.all { char -> char.isDigit() }) year = it },
                label = { Text("Year") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = vin,
                onValueChange = { if (it.length <= 17) vin = it.uppercase() },
                label = { Text("VIN (Optional - 17 characters)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending,
                supportingText = {
                    if (vin.isNotEmpty()) {
                        Text("${vin.length}/17")
                    }
                },
                isError = vin.isNotEmpty() && vin.length != 17
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = power,
                    onValueChange = { if (it.all { char -> char.isDigit() }) power = it },
                    label = { Text("Power") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = state !is AddCarState.Pending
                )
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.width(100.dp)) {
                    OutlinedTextField(
                        value = powerUnit,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                "dropdown",
                                Modifier.clickable { powerUnitExpanded = true })
                        },
                        modifier = Modifier.clickable { powerUnitExpanded = true }
                    )
                    // Overlay
                    Box(modifier = Modifier.matchParentSize().clickable { powerUnitExpanded = true })

                    DropdownMenu(
                        expanded = powerUnitExpanded,
                        onDismissRequest = { powerUnitExpanded = false }
                    ) {
                        powerUnits.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    powerUnit = unit
                                    powerUnitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = engineSize,
                onValueChange = { engineSize = it },
                label = { Text("Engine Size (e.g. 2.0L)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fuelType,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Fuel Type") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            "dropdown",
                            Modifier.clickable { fuelTypeExpanded = true })
                    }
                )
                // Transparent click layer
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { fuelTypeExpanded = true }
                )
                DropdownMenu(
                    expanded = fuelTypeExpanded,
                    onDismissRequest = { fuelTypeExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    fuelTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                fuelType = type
                                fuelTypeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.onAddOrUpdateCar(
                        name = name,
                        plateCountry = selectedCountry.code,
                        make = make,
                        model = model,
                        vin = vin,
                        year = year,
                        engineSize = engineSize,
                        fuelType = fuelType,
                        color = color,
                        power = power,
                        powerUnit = powerUnit
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && state !is AddCarState.Pending
            ) {
                if (state is AddCarState.Pending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (carId == null) "Save Car" else "Update Car")
                }
            }
        }
    }
}
