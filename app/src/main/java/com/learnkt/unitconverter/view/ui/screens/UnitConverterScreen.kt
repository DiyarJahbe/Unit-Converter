package com.learnkt.unitconverter.ui.screens

import CategorySelector
import HistorySection
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.learnkt.unitconverter.ui.components.InputCard
import com.learnkt.unitconverter.view.ui.components.ResultCard
import com.learnkt.unitconverter.viewmodel.ConverterViewModel

import kotlinx.coroutines.flow.collectLatest

@Composable
fun UnitConverterScreen(
    modifier: Modifier,
    viewModel: ConverterViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ConverterViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CategorySelector(
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = viewModel::onCategorySelected
        )

        InputCard(
            uiState = uiState,
            onInputValueChange = viewModel::onInputValueChange,
            onInputUnitSelected = viewModel::onInputUnitSelected,
            onOutputUnitSelected = viewModel::onOutputUnitSelected,
            onSwapUnits = viewModel::swapUnits,
            onConvert = viewModel::performConversion,
            onInputDropdownToggle = viewModel::onInputDropdownToggled,
            onOutputDropdownToggle = viewModel::onOutputDropdownToggled
        )

        ResultCard(
            result = uiState.result,
            outputUnit = uiState.outputUnit
        )

        HistorySection(
            history = uiState.history,
            showHistory = uiState.showHistory,
            onClearHistory = viewModel::clearHistory,
            onToggleHistory = viewModel::toggleHistory
        )
    }
}