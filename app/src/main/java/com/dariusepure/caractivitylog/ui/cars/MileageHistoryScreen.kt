package com.dariusepure.caractivitylog.ui.cars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dariusepure.caractivitylog.domain.MileageLog
import com.dariusepure.caractivitylog.ui.common.CarFormatters
import com.dariusepure.caractivitylog.ui.common.LoadingState
import com.dariusepure.caractivitylog.ui.common.MileageItem
import com.dariusepure.caractivitylog.ui.common.ErrorState
import com.dariusepure.caractivitylog.domain.displayName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MileageHistoryScreen(
    carId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CarDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddMileageDialog by remember { mutableStateOf(false) }
    var editingMileageLog by remember { mutableStateOf<MileageLog?>(null) }

    LaunchedEffect(carId) {
        viewModel.loadCarData(carId)
    }

    if (showAddMileageDialog || editingMileageLog != null) {
        val existingLogs = (state as? CarDetailsUiState.Success)?.mileageLogs ?: emptyList()
        val car = (state as? CarDetailsUiState.Success)?.car
        val country = europeanCountries.find { it.code == car?.plateCountry }
        val unitLabel = if (country?.usesMiles == true) "mi" else "km"

        AddMileageDialog(
            existingLog = editingMileageLog,
            existingLogs = existingLogs,
            unit = unitLabel,
            onDismiss = { 
                showAddMileageDialog = false
                editingMileageLog = null
            },
            onConfirm = { value, date ->
                val usesMiles = country?.usesMiles == true
                val canonicalValue = CarFormatters.toCanonicalDistance(value, usesMiles)
                
                val logToEdit = editingMileageLog
                if (logToEdit != null) {
                    viewModel.updateMileage(carId, logToEdit.copy(km = canonicalValue, date = date))
                } else {
                    viewModel.addMileage(carId, canonicalValue, date)
                }
                showAddMileageDialog = false
                editingMileageLog = null
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Mileage History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMileageDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Mileage")
            }
        }
    ) { padding ->
        when (val s = state) {
            CarDetailsUiState.Loading -> LoadingState()
            is CarDetailsUiState.Error -> ErrorState(message = s.message, onRetry = { viewModel.loadCarData(carId) })
            is CarDetailsUiState.Success -> {
                val car = s.car
                val country = europeanCountries.find { it.code == car.plateCountry }
                val unitLabel = if (country?.usesMiles == true) "mi" else "km"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = car.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    if (s.mileageLogs.isEmpty()) {
                        item {
                            Text(
                                text = "No mileage records found.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(s.mileageLogs) { log ->
                            val displayValue = CarFormatters.fromCanonicalDistance(log.km, country?.usesMiles == true)
                            MileageItem(
                                log = log.copy(km = displayValue),
                                unit = unitLabel,
                                onEditClick = { editingMileageLog = log },
                                onDeleteClick = { viewModel.deleteMileage(carId, log.id) }
                            )
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
    }
}
