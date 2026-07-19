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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.sizeIn

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
    var torque by remember { mutableStateOf("") }
    var engineCode by remember { mutableStateOf("") }

    var countryExpanded by remember { mutableStateOf(false) }
    var fuelTypeExpanded by remember { mutableStateOf(false) }
    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "Hybrid", "LPG")

    var colorExpanded by remember { mutableStateOf(false) }
    val carColors = listOf(
        "White", "Black", "Silver", "Grey", "Blue", "Red", "Brown", 
        "Green", "Orange", "Beige", "Yellow", "Gold", "Purple", "Pink"
    ).sorted()

    var powerUnitExpanded by remember { mutableStateOf(false) }
    val powerUnits = listOf("hp", "kw")

    val handleBack = {
        if (carId != null && name.isNotBlank() && (vin.isEmpty() || vin.length == 17)) {
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
                powerUnit = powerUnit,
                torque = torque,
                engineCode = engineCode
            )
        } else {
            onBack()
        }
    }

    BackHandler(onBack = handleBack)

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
                torque = car.torque.takeIf { it != 0 }?.toString() ?: ""
                engineCode = car.engineCode
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
                    IconButton(onClick = handleBack) {
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
                    enabled = state !is AddCarState.Pending
                )
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = make,
                onValueChange = { make = it },
                label = { Text("Make") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model") },
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
                onValueChange = { input ->
                    val filtered = input.uppercase().filter { it.isLetterOrDigit() && it !in listOf('I', 'O', 'Q') }
                    if (filtered.length <= 17) vin = filtered
                },
                label = { Text("VIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending,
                supportingText = {
                    Column {
                        if (vin.isNotEmpty()) {
                            Text("${vin.length}/17")
                            if (vin.length < 17) {
                                Text("Remaining: ${17 - vin.length} characters", color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Text("Letters I, O, Q are not allowed", style = MaterialTheme.typography.bodySmall)
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
                value = torque,
                onValueChange = { if (it.all { char -> char.isDigit() }) torque = it },
                label = { Text("Torque (Nm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = engineCode,
                onValueChange = { engineCode = it.uppercase() },
                label = { Text("Engine Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = engineSize,
                onValueChange = { engineSize = it },
                label = { Text("Engine Size") },
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

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = color,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Color") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            "dropdown",
                            Modifier.clickable { colorExpanded = true })
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { colorExpanded = true }
                )
                DropdownMenu(
                    expanded = colorExpanded,
                    onDismissRequest = { colorExpanded = false },
                    modifier = Modifier.sizeIn(maxHeight = 300.dp)
                ) {
                    carColors.forEach { colorOption ->
                        DropdownMenuItem(
                            text = { Text(colorOption) },
                            onClick = {
                                color = colorOption
                                colorExpanded = false
                            }
                        )
                    }
                }
            }

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
                        powerUnit = powerUnit,
                        torque = torque,
                        engineCode = engineCode
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
