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
import androidx.compose.ui.text.style.TextAlign
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
    var customMake by remember { mutableStateOf("") }
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
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var fuelTankCapacity by remember { mutableStateOf("") }
    var drivetrain by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("") }

    var countryExpanded by remember { mutableStateOf(false) }
    var makeExpanded by remember { mutableStateOf(false) }
    var fuelTypeExpanded by remember { mutableStateOf(false) }
    var drivetrainExpanded by remember { mutableStateOf(false) }
    var vehicleTypeExpanded by remember { mutableStateOf(false) }
    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "Hybrid", "LPG")
    val drivetrainOptions = listOf("FWD", "RWD", "AWD", "4WD")
    val vehicleTypes = listOf("Saloon", "Estate", "Hatchback", "MPV", "SUV", "Coupe", "Convertible", "Van", "Pickup")

    var colorExpanded by remember { mutableStateOf(false) }
    val carColors = listOf(
        "White", "Black", "Silver", "Grey", "Blue", "Red", "Brown", 
        "Green", "Orange", "Beige", "Yellow", "Gold", "Purple", "Pink"
    ).sorted()

    var powerUnitExpanded by remember { mutableStateOf(false) }
    val powerUnits = listOf("hp", "kw")

    val handleBack = {
        val finalMake = if (make == "Other") customMake else make
        if (carId != null && finalMake.isNotBlank() && model.isNotBlank() && (vin.isEmpty() || vin.length == 17)) {
            viewModel.onAddOrUpdateCar(
                name = name,
                plateCountry = selectedCountry.code,
                make = finalMake,
                model = model,
                vin = vin,
                year = year,
                engineSize = engineSize,
                fuelType = fuelType,
                color = color,
                power = power,
                powerUnit = powerUnit,
                torque = torque,
                engineCode = engineCode,
                length = length,
                width = width,
                height = height,
                fuelTankCapacity = fuelTankCapacity,
                drivetrain = drivetrain,
                vehicleType = vehicleType
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
                if (carBrands.contains(car.make) && car.make != "Other") {
                    make = car.make
                } else {
                    make = "Other"
                    customMake = car.make
                }
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
                length = car.length.takeIf { it != 0 }?.toString() ?: ""
                width = car.width.takeIf { it != 0 }?.toString() ?: ""
                height = car.height.takeIf { it != 0 }?.toString() ?: ""
                fuelTankCapacity = car.fuelTankCapacity.takeIf { it != 0.0 }?.toString() ?: ""
                drivetrain = car.drivetrain
                vehicleType = car.vehicleType
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
                        value = selectedCountry.code,
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
                                text = { Text(country.name) },
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
                    label = { Text("License Plate (Optional)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    enabled = state !is AddCarState.Pending,
                    supportingText = {
                        selectedCountry.plateHint?.let { hint ->
                            Text("Ex: $hint", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = make,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Make *") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            "dropdown",
                            Modifier.clickable { makeExpanded = true })
                    },
                    isError = state !is AddCarState.Pending && make.isBlank()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { makeExpanded = true }
                )
                DropdownMenu(
                    expanded = makeExpanded,
                    onDismissRequest = { makeExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f).sizeIn(maxHeight = 300.dp)
                ) {
                    carBrands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text(brand) },
                            onClick = {
                                make = brand
                                if (brand != "Other") customMake = ""
                                makeExpanded = false
                            }
                        )
                    }
                }
            }

            if (make == "Other") {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customMake,
                    onValueChange = { customMake = it },
                    label = { Text("Brand Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = state !is AddCarState.Pending,
                    isError = state !is AddCarState.Pending && customMake.isBlank()
                )
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending,
                isError = state !is AddCarState.Pending && model.isBlank()
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

            Spacer(Modifier.height(16.dp))
            Text(
                "Dimensions (mm)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = length,
                    onValueChange = { if (it.all { char -> char.isDigit() }) length = it },
                    label = { Text("Length") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = state !is AddCarState.Pending
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = width,
                    onValueChange = { if (it.all { char -> char.isDigit() }) width = it },
                    label = { Text("Width") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = state !is AddCarState.Pending
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = height,
                    onValueChange = { if (it.all { char -> char.isDigit() }) height = it },
                    label = { Text("Height") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = state !is AddCarState.Pending
                )
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = engineSize,
                onValueChange = { engineSize = it },
                label = { Text("Engine Size (cc)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = fuelTankCapacity,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) fuelTankCapacity = it },
                label = { Text("Fuel Tank Capacity (L)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = vehicleType,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Vehicle Type") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            "dropdown",
                            Modifier.clickable { vehicleTypeExpanded = true })
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { vehicleTypeExpanded = true }
                )
                DropdownMenu(
                    expanded = vehicleTypeExpanded,
                    onDismissRequest = { vehicleTypeExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f).sizeIn(maxHeight = 300.dp)
                ) {
                    vehicleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                vehicleType = type
                                vehicleTypeExpanded = false
                            }
                        )
                    }
                }
            }

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
                    value = drivetrain,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Drivetrain") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            "dropdown",
                            Modifier.clickable { drivetrainExpanded = true })
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { drivetrainExpanded = true }
                )
                DropdownMenu(
                    expanded = drivetrainExpanded,
                    onDismissRequest = { drivetrainExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    drivetrainOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                drivetrain = option
                                drivetrainExpanded = false
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
                    val finalMake = if (make == "Other") customMake else make
                    viewModel.onAddOrUpdateCar(
                        name = name,
                        plateCountry = selectedCountry.code,
                        make = finalMake,
                        model = model,
                        vin = vin,
                        year = year,
                        engineSize = engineSize,
                        fuelType = fuelType,
                        color = color,
                        power = power,
                        powerUnit = powerUnit,
                        torque = torque,
                        engineCode = engineCode,
                        length = length,
                        width = width,
                        height = height,
                        fuelTankCapacity = fuelTankCapacity,
                        drivetrain = drivetrain,
                        vehicleType = vehicleType
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = (if (make == "Other") customMake.isNotBlank() else make.isNotBlank()) && 
                         model.isNotBlank() && state !is AddCarState.Pending
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
