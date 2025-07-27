package com.learnkt.unitconverter.ui.components

import UnitDropdown
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnkt.unitconverter.viewmodel.ConverterUiState

@Composable
fun InputCard(
    uiState: ConverterUiState,
    onInputValueChange: (String) -> Unit,
    onInputUnitSelected: (String) -> Unit,
    onOutputUnitSelected: (String) -> Unit,
    onSwapUnits: () -> Unit,
    onConvert: () -> Unit,
    onInputDropdownToggle: (Boolean) -> Unit,
    onOutputDropdownToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.inputValue,
                onValueChange = onInputValueChange,
                label = { Text("Enter Value") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Input, null) },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                UnitDropdown(
                    expanded = uiState.iExpanded,
                    onDismiss = { onInputDropdownToggle(false) },
                    onExpandChange = { onInputDropdownToggle(true) },
                    selectedUnit = uiState.inputUnit,
                    onUnitSelected = onInputUnitSelected,
                    units = uiState.currentUnits,
                    label = "From",
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onSwapUnits,
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Filled.CompareArrows, "Swap units", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }


                UnitDropdown(
                    expanded = uiState.oExpanded,
                    onDismiss = { onOutputDropdownToggle(false) },
                    onExpandChange = { onOutputDropdownToggle(true) },
                    selectedUnit = uiState.outputUnit,
                    onUnitSelected = onOutputUnitSelected,
                    units = uiState.currentUnits,
                    label = "To",
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = onConvert,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = uiState.inputValue.isNotEmpty() && uiState.inputUnit.isNotEmpty() && uiState.outputUnit.isNotEmpty()
            ) {
                Text("Convert", fontSize = 16.sp)
            }
        }
    }
}