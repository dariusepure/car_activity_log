package com.dariusepure.caractivitylog.ui.cars

import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dariusepure.caractivitylog.domain.MileageLog
import com.dariusepure.caractivitylog.ui.common.ErrorState
import com.dariusepure.caractivitylog.ui.common.LoadingState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(
    carId: String,
    onBack: () -> Unit,
    onMileageClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CarDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // TODO: Upload image to Firebase Storage and update Car document
        }
    }

    LaunchedEffect(carId) {
        viewModel.loadCarData(carId)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Car Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo")
            }
        }
    ) { padding ->
        when (val s = state) {
            CarDetailsUiState.Loading -> LoadingState()
            is CarDetailsUiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadCarData(carId) })
            is CarDetailsUiState.Success -> {
                val car = s.car
                val hpValue: Int
                val kwValue: Int
                
                if (car.powerUnit.lowercase() == "kw") {
                    kwValue = car.power
                    hpValue = (car.power * 1.34102).toInt()
                } else {
                    hpValue = car.power
                    kwValue = (car.power / 1.34102).toInt()
                }

                val flag = europeanCountries.find { it.code == car.plateCountry }?.flag ?: "🏳️"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = flag,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = car.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        SpecificationCard(
                            specifications = listOf(
                                "Make" to car.make,
                                "Model" to car.model,
                                "Year" to car.year.toString(),
                                "Power" to "$hpValue hp / $kwValue kw",
                                "Engine" to car.engineSize,
                                "Fuel" to car.fuelType,
                                "Color" to car.color,
                                "VIN" to car.vin
                            )
                        )
                        Spacer(Modifier.height(24.dp))
                        
                        Card(
                            onClick = onMileageClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Mileage History",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "View and manage records",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(80.dp)) // Space for FAB
                    }
                }
            }
        }
    }
}

@Composable
fun MileageItem(
    log: MileageLog,
    unit: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${log.km.toInt()} $unit",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = dateFormat.format(log.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit mileage",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete mileage",
                tint = androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}

@Composable
fun AddMileageDialog(
    existingLog: MileageLog? = null,
    existingLogs: List<MileageLog> = emptyList(),
    unit: String = "km",
    onDismiss: () -> Unit,
    onConfirm: (Double, Date) -> Unit
) {
    var km by remember { mutableStateOf(existingLog?.km?.toInt()?.toString() ?: "") }
    var selectedDate by remember { mutableStateOf(existingLog?.date ?: Date()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(year, month, dayOfMonth)
            selectedDate = newCalendar.time
            errorMessage = null
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingLog == null) "Add Mileage" else "Edit Mileage") },
        text = {
            Column {
                OutlinedTextField(
                    value = km,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() }) {
                            km = it
                            errorMessage = null
                        }
                    },
                    label = { Text("Distance ($unit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null
                )
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = dateFormat.format(selectedDate),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Select Date")
                        }
                    },
                    enabled = false,
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val kmInt = km.toIntOrNull() ?: 0
                    if (kmInt > 0) {
                        val kmDouble = kmInt.toDouble()
                        val conflict = existingLogs.find { log ->
                            if (log.id == existingLog?.id) return@find false
                            val kmBackwards = selectedDate.after(log.date) && kmDouble < log.km
                            val dateBackwards = selectedDate.before(log.date) && kmDouble > log.km
                            kmBackwards || dateBackwards
                        }

                        if (conflict != null) {
                            errorMessage = if (selectedDate.after(conflict.date)) {
                                "Cannot be less than ${conflict.km.toInt()} $unit (recorded on ${dateFormat.format(conflict.date)})"
                            } else {
                                "Cannot be more than ${conflict.km.toInt()} $unit (recorded on ${dateFormat.format(conflict.date)})"
                            }
                        } else {
                            onConfirm(kmDouble, selectedDate)
                        }
                    }
                },
                enabled = km.isNotBlank()
            ) {
                Text(if (existingLog == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SpecificationCard(specifications: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            specifications.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value.ifBlank { "-" },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (index < specifications.size - 1) {
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
