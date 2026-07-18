package com.dariusepure.caractivitylog.ui.cars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var engineSize by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    LaunchedEffect(carId) {
        if (carId != null) {
            viewModel.loadCar(carId)
            val car = viewModel.getCarData(carId)
            if (car != null) {
                name = car.name
                make = car.make
                model = car.model
                vin = car.vin
                year = car.year.takeIf { it != 0 }?.toString() ?: ""
                engineSize = car.engineSize
                fuelType = car.fuelType
                color = car.color
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = error.message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nickname (e.g. My Daily)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

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
                onValueChange = { year = it },
                label = { Text("Year") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = state !is AddCarState.Pending
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = vin,
                onValueChange = { vin = it },
                label = { Text("VIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

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

            OutlinedTextField(
                value = fuelType,
                onValueChange = { fuelType = it },
                label = { Text("Fuel Type (e.g. Diesel)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state !is AddCarState.Pending
            )

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
                        make = make,
                        model = model,
                        vin = vin,
                        year = year,
                        engineSize = engineSize,
                        fuelType = fuelType,
                        color = color
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