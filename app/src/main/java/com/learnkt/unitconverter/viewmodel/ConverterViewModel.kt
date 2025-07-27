package com.learnkt.unitconverter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.unitconverter.model.data.ConverterModel
import com.learnkt.unitconverter.model.data.ConversionCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Data class to hold the entire UI state
data class ConverterUiState(
    val inputValue: String = "",
    val inputUnit: String = ConverterModel.unitMap[ConversionCategory.LENGTH]?.first() ?: "",
    val outputUnit: String = ConverterModel.unitMap[ConversionCategory.LENGTH]?.getOrNull(1) ?: "",
    val result: String = "",
    val iExpanded: Boolean = false,
    val oExpanded: Boolean = false,
    val selectedCategory: ConversionCategory = ConversionCategory.LENGTH,
    val history: List<String> = emptyList(),
    val showHistory: Boolean = false,
    val currentUnits: List<String> = ConverterModel.unitMap[ConversionCategory.LENGTH] ?: emptyList()
)

// ViewModel to manage UI state and business logic
class ConverterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onInputValueChange(value: String) {
        _uiState.update { it.copy(inputValue = value, result = "") }
    }

    fun onInputUnitSelected(unit: String) {
        _uiState.update { it.copy(inputUnit = unit, iExpanded = false, result = "") }
    }

    fun onOutputUnitSelected(unit: String) {
        _uiState.update { it.copy(outputUnit = unit, oExpanded = false, result = "") }
    }

    fun onCategorySelected(category: ConversionCategory) {
        val newUnits = ConverterModel.unitMap[category] ?: emptyList()
        _uiState.update {
            it.copy(
                selectedCategory = category,
                currentUnits = newUnits,
                inputValue = "",
                result = "",
                inputUnit = newUnits.getOrElse(0) { "" },
                outputUnit = newUnits.getOrElse(1) { "" }
            )
        }
    }

    fun swapUnits() {
        _uiState.update { currentState ->
            currentState.copy(
                inputUnit = currentState.outputUnit,
                outputUnit = currentState.inputUnit,
                inputValue = if (currentState.result.isNotEmpty()) currentState.result else currentState.inputValue,
                result = ""
            )
        }
    }

    fun performConversion() {
        val currentState = _uiState.value
        val inputDouble = currentState.inputValue.toDoubleOrNull()

        if (inputDouble == null || currentState.inputUnit.isEmpty() || currentState.outputUnit.isEmpty()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowToast("Please enter a valid number and select units"))
            }
            return
        }

        val conversionResult = ConverterModel.convert(inputDouble, currentState.inputUnit, currentState.outputUnit, currentState.selectedCategory)
        val resultString = String.format("%.4f", conversionResult)
        val historyEntry = "${currentState.inputValue} ${currentState.inputUnit} = $resultString ${currentState.outputUnit}"

        _uiState.update {
            it.copy(
                result = resultString,
                history = listOf(historyEntry) + it.history
            )
        }
    }

    fun toggleHistory() {
        _uiState.update { it.copy(showHistory = !it.showHistory) }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun onInputDropdownToggled(isExpanded: Boolean) {
        _uiState.update { it.copy(iExpanded = isExpanded) }
    }

    fun onOutputDropdownToggled(isExpanded: Boolean) {
        _uiState.update { it.copy(oExpanded = isExpanded) }
    }

    // Sealed class for one-off UI events like showing a Toast
    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
    }
}