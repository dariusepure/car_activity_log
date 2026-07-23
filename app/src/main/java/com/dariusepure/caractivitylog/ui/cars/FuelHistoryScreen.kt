package com.dariusepure.caractivitylog.ui.cars

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dariusepure.caractivitylog.ui.common.CarFormatters
import com.dariusepure.caractivitylog.ui.common.ErrorState
import com.dariusepure.caractivitylog.ui.common.LoadingState
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelHistoryScreen(
    carId: String,
    onBack: () -> Unit,
    viewModel: FuelHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(carId) {
        viewModel.loadData(carId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fuel Consumption") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Filling")
            }
        }
    ) { padding ->
        when (val s = state) {
            FuelHistoryUiState.Loading -> LoadingState()
            is FuelHistoryUiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadData(carId) })
            is FuelHistoryUiState.Success -> {
                val country = europeanCountries.find { it.code == s.car.plateCountry }
                val usesMiles = country?.usesMiles == true
                val distUnit = if (usesMiles) "mi" else "km"
                val consUnit = if (usesMiles) "mpg" else "L/100km"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        FuelStatsCard(s.stats, distUnit, consUnit)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Filling History",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (s.logs.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No records yet. Add your first filling!", color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }

                    items(s.logs) { entry ->
                        FuelLogItem(
                            entry = entry,
                            distUnit = distUnit,
                            consUnit = consUnit,
                            usesMiles = usesMiles,
                            onDelete = { viewModel.deleteFuelLog(carId, entry.log.id) }
                        )
                    }
                    
                    item { Spacer(Modifier.height(80.dp)) }
                }

                if (showAddDialog) {
                    AddFuelDialog(
                        unit = distUnit,
                        onDismiss = { showAddDialog = false },
                        onConfirm = { km, liters, cost, isFull, date ->
                            viewModel.addFuelLog(carId, km, liters, cost, isFull, date)
                            showAddDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FuelStatsCard(stats: FuelStats, distUnit: String, consUnit: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Avg Consumption", String.format(Locale.getDefault(), "%.2f %s", stats.avgConsumption, consUnit))
                StatItem("Total Distance", "${stats.totalDistance.roundToInt()} $distUnit")
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Total Fuel", String.format(Locale.getDefault(), "%.1f L", stats.totalLiters))
                StatItem("Total Cost", String.format(Locale.getDefault(), "%.2f", stats.totalCost))
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun FuelLogItem(
    entry: FuelLogWithConsumption,
    distUnit: String,
    consUnit: String,
    usesMiles: Boolean,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocalGasStation, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(dateFormat.format(entry.log.date), style = MaterialTheme.typography.labelSmall)
                Text(
                    text = "${CarFormatters.fromCanonicalDistance(entry.log.km, usesMiles).roundToInt()} $distUnit",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${entry.log.liters} L • ${entry.log.cost} • ${if (entry.log.isFullTank) "Full Tank" else "Partial"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (entry.consumption != null) {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f", entry.consumption),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(consUnit, style = MaterialTheme.typography.labelSmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddFuelDialog(
    unit: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double, Double, Boolean, Date) -> Unit
) {
    var km by remember { mutableStateOf("") }
    var liters by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var isFullTank by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(Date()) }
    
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Filling") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = km,
                    onValueChange = { if (it.all { c -> c.isDigit() }) km = it },
                    label = { Text("Mileage ($unit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = liters,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) liters = it },
                        label = { Text("Liters") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) cost = it },
                        label = { Text("Total Cost") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFullTank, onCheckedChange = { isFullTank = it })
                    Text("Full Tank", modifier = Modifier.clickable { isFullTank = !isFullTank })
                }

                OutlinedTextField(
                    value = dateFormat.format(selectedDate),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val k = km.toDoubleOrNull() ?: 0.0
                    val l = liters.toDoubleOrNull() ?: 0.0
                    val c = cost.toDoubleOrNull() ?: 0.0
                    if (k > 0 && l > 0) {
                        onConfirm(k, l, c, isFullTank, selectedDate)
                    }
                },
                enabled = km.isNotBlank() && liters.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
