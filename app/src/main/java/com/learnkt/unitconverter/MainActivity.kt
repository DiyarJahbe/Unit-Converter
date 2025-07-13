package com.learnkt.unitconverter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learnkt.unitconverter.ui.theme.*

// Main entry point of the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enables drawing behind the system bars
        enableEdgeToEdge()
        // Sets the main UI content of the activity
        setContent {
            UnitConverterTheme {
                UnitConverterApp()
            }
        }
    }
}

// Sets up the main app layout with a top bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Unit Converter",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // The main screen content is placed here
    ) { innerPadding ->
        UnitConverterScreen(Modifier.padding(innerPadding))
    }
}

// Defines the different categories for conversion
enum class ConversionCategory {
    LENGTH, WEIGHT, VOLUME, TEMPERATURE, TIME
}

// Maps each conversion category to a list of its available units
val unitMap = mapOf(
    ConversionCategory.LENGTH to listOf(
        "Millimeter",
        "Centimeter",
        "Meter",
        "Kilometer",
        "Inch",
        "Feet",
        "Yard",
        "Mile"
    ),
    ConversionCategory.WEIGHT to listOf("Milligram", "Gram", "Kilogram", "Pound", "Ounce", "Ton"),
    ConversionCategory.VOLUME to listOf(
        "Milliliter",
        "Liter",
        "Cubic Meter",
        "Teaspoon",
        "Tablespoon",
        "Cup",
        "Pint",
        "Quart",
        "Gallon"
    ),
    ConversionCategory.TEMPERATURE to listOf("Celsius", "Fahrenheit", "Kelvin"),
    ConversionCategory.TIME to listOf("Second", "Minute", "Hour", "Day", "Week", "Month", "Year")
)

// Maps the full unit names to their shorter abbreviations
val unitAbbreviations = mapOf(
    "Millimeter" to "mm",
    "Centimeter" to "cm",
    "Meter" to "m",
    "Kilometer" to "km",
    "Inch" to "in",
    "Feet" to "ft",
    "Yard" to "yd",
    "Mile" to "mi",
    "Milligram" to "mg",
    "Gram" to "g",
    "Kilogram" to "kg",
    "Pound" to "lb",
    "Ounce" to "oz",
    "Ton" to "t",
    "Milliliter" to "ml",
    "Liter" to "L",
    "Cubic Meter" to "m³",
    "Teaspoon" to "tsp",
    "Tablespoon" to "tbsp",
    "Cup" to "cup",
    "Pint" to "pt",
    "Quart" to "qt",
    "Gallon" to "gal",
    "Celsius" to "°C",
    "Fahrenheit" to "°F",
    "Kelvin" to "K",
    "Second" to "s",
    "Minute" to "min",
    "Hour" to "hr",
    "Day" to "day",
    "Week" to "wk",
    "Month" to "mo",
    "Year" to "yr"
)

// The main screen composable that holds all UI elements and state
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(modifier: Modifier = Modifier) {
    // Gets the current context, useful for showing Toasts
    val context = LocalContext.current

    // State variables to hold user input and results
    var inputValue by remember { mutableStateOf("") }
    var inputUnit by remember { mutableStateOf("") }
    var outputUnit by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var iExpanded by remember { mutableStateOf(false) } // State for input dropdown menu
    var oExpanded by remember { mutableStateOf(false) } // State for output dropdown menu
    var selectedCategory by remember { mutableStateOf(ConversionCategory.LENGTH) }
    val history = remember { mutableStateListOf<String>() } // List to store conversion history
    var showHistory by remember { mutableStateOf(false) } // State to toggle history visibility

    // Gets the list of units for the currently selected category
    val currentUnits = unitMap[selectedCategory] ?: emptyList()

    // Resets the input fields when the conversion category changes
    LaunchedEffect(key1 = selectedCategory) {
        inputValue = ""
        result = ""
        inputUnit = currentUnits.getOrElse(0) { "" }
        outputUnit = currentUnits.getOrElse(1) { "" }
    }

    // Main layout column for the screen
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Displays the category selection buttons
        CategorySelector(selectedCategory) { selectedCategory = it }

        // Card containing the input fields and convert button
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
                // Text field for the user to enter a value
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text("Enter Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Input, null) },
                    singleLine = true
                )

                // Row for the two unit dropdowns and the swap button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "From" unit dropdown
                    UnitDropdown(
                        iExpanded,
                        { iExpanded = false },
                        { iExpanded = true },
                        inputUnit,
                        { inputUnit = it; result = "" },
                        currentUnits,
                        "From",
                        Modifier.weight(1f)
                    )

                    // Button to swap the input and output units
                    IconButton(
                        onClick = {
                            val temp = inputUnit
                            inputUnit = outputUnit
                            outputUnit = temp
                            // If a result exists, move it to the input value
                            if (result.isNotEmpty()) {
                                inputValue = result
                                result = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Filled.CompareArrows,
                            contentDescription = "Swap units",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // "To" unit dropdown
                    UnitDropdown(
                        oExpanded,
                        { oExpanded = false },
                        { oExpanded = true },
                        outputUnit,
                        { outputUnit = it; result = "" },
                        currentUnits,
                        "To",
                        Modifier.weight(1f)
                    )
                }

                // Button to trigger the conversion
                Button(
                    onClick = {
                        val inputDouble = inputValue.toDoubleOrNull()
                        // Checks for valid input before converting
                        if (inputDouble == null || inputUnit.isEmpty() || outputUnit.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please enter a valid number and select units",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Performs the conversion and updates the result state
                            val conversionResult =
                                convert(inputDouble, inputUnit, outputUnit, selectedCategory)
                            result = String.format("%.4f", conversionResult)
                            // Adds the conversion to the history list
                            history.add(0, "$inputValue $inputUnit = $result $outputUnit")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    // Button is disabled if input fields are empty
                    enabled = inputValue.isNotEmpty() && inputUnit.isNotEmpty() && outputUnit.isNotEmpty()
                ) {
                    Text("Convert", fontSize = 16.sp)
                }
            }
        }

        // Animated visibility for the result card, only shows when there is a result
        AnimatedVisibility(
            visible = result.isNotEmpty(),
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Result",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        result,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        outputUnit,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Displays the history section
        HistorySection(history, showHistory, { history.clear() }, { showHistory = !showHistory })
    }
}

// A reusable composable for the unit selection dropdown menus
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    expanded: Boolean, onDismiss: () -> Unit, onExpandChange: () -> Unit,
    selectedUnit: String, onUnitSelected: (String) -> Unit, units: List<String>,
    label: String, modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable { onExpandChange() }) {
        // A disabled text field that looks like a dropdown button
        OutlinedTextField(
            // Displays the abbreviation of the selected unit
            value = unitAbbreviations[selectedUnit] ?: selectedUnit,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false, // Prevents user from typing in it
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        // The actual dropdown menu that appears when clicked
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    // The dropdown list shows the full, descriptive name
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit) // Updates the selected unit
                        onDismiss() // Closes the dropdown
                    }
                )
            }
        }
    }
}

// A row of buttons for selecting the conversion category
@Composable
fun CategorySelector(
    selectedCategory: ConversionCategory,
    onCategorySelected: (ConversionCategory) -> Unit
) {
    // List of categories with their corresponding icons
    val categories: List<Pair<ConversionCategory, ImageVector>> = listOf(
        ConversionCategory.LENGTH to Icons.Filled.Straighten,
        ConversionCategory.WEIGHT to Icons.Filled.Scale,
        ConversionCategory.VOLUME to Icons.Filled.WaterDrop,
        ConversionCategory.TEMPERATURE to Icons.Filled.Thermostat,
        ConversionCategory.TIME to Icons.Filled.Schedule
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Creates a button for each category
        categories.forEach { (category, icon) ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCategorySelected(category) }
                    // Changes background color if the category is selected
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        icon,
                        contentDescription = category.name,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = category.name.take(4).uppercase(), // Shows a short name
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// A card that displays the conversion history
@Composable
fun HistorySection(
    history: List<String>,
    showHistory: Boolean,
    onClearHistory: () -> Unit,
    onToggleHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            // Row containing the title and control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                // Button to show or hide the history list
                IconButton(onClick = onToggleHistory) {
                    val icon =
                        if (showHistory) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                    Icon(
                        icon,
                        contentDescription = if (showHistory) "Hide history" else "Show history"
                    )
                }
                // Button to clear all history items
                IconButton(onClick = onClearHistory, enabled = history.isNotEmpty()) {
                    Icon(Icons.Filled.Delete, contentDescription = "Clear history")
                }
            }
            // The list of history items, which is only visible when toggled
            AnimatedVisibility(visible = showHistory && history.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(history) { item ->
                        Text(
                            item,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
            }
        }
    }
}

// Main conversion function that calls the correct specific conversion logic
fun convert(value: Double, fromUnit: String, toUnit: String, category: ConversionCategory): Double =
    when (category) {
        ConversionCategory.LENGTH -> convertLength(value, fromUnit, toUnit)
        ConversionCategory.WEIGHT -> convertWeight(value, fromUnit, toUnit)
        ConversionCategory.VOLUME -> convertVolume(value, fromUnit, toUnit)
        ConversionCategory.TEMPERATURE -> convertTemperature(value, fromUnit, toUnit)
        ConversionCategory.TIME -> convertTime(value, fromUnit, toUnit)
    }

// A generic conversion function that works for units with a common base (e.g., meters for length)
fun convertWithFactors(
    value: Double,
    fromUnit: String,
    toUnit: String,
    factors: Map<String, Double>
): Double {
    if (fromUnit == toUnit) return value
    val fromFactor = factors[fromUnit] ?: return 0.0 // Factor to convert 'from' unit to base unit
    val toFactor = factors[toUnit] ?: return 0.0   // Factor to convert 'to' unit to base unit
    return value * fromFactor / toFactor // Convert to base unit, then to target unit
}

// Provides length conversion factors (base unit: Meter)
fun convertLength(value: Double, fromUnit: String, toUnit: String) = convertWithFactors(
    value, fromUnit, toUnit, mapOf(
        "Millimeter" to 0.001, "Centimeter" to 0.01, "Meter" to 1.0, "Kilometer" to 1000.0,
        "Inch" to 0.0254, "Feet" to 0.3048, "Yard" to 0.9144, "Mile" to 1609.34
    )
)

// Provides weight conversion factors (base unit: Kilogram)
fun convertWeight(value: Double, fromUnit: String, toUnit: String) = convertWithFactors(
    value, fromUnit, toUnit, mapOf(
        "Milligram" to 0.000001, "Gram" to 0.001, "Kilogram" to 1.0,
        "Pound" to 0.453592, "Ounce" to 0.0283495, "Ton" to 1000.0
    )
)

// Provides volume conversion factors (base unit: Liter)
fun convertVolume(value: Double, fromUnit: String, toUnit: String) = convertWithFactors(
    value, fromUnit, toUnit, mapOf(
        "Milliliter" to 0.001, "Liter" to 1.0, "Cubic Meter" to 1000.0,
        "Teaspoon" to 0.00492892, "Tablespoon" to 0.0147868, "Cup" to 0.24,
        "Pint" to 0.473176, "Quart" to 0.946353, "Gallon" to 3.78541
    )
)

// Handles temperature conversion, which has unique formulas and no simple base unit
fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
    if (fromUnit == toUnit) return value
    return when (fromUnit) {
        "Celsius" -> when (toUnit) {
            "Fahrenheit" -> (value * 9 / 5) + 32
            "Kelvin" -> value + 273.15
            else -> value
        }

        "Fahrenheit" -> when (toUnit) {
            "Celsius" -> (value - 32) * 5 / 9
            "Kelvin" -> (value - 32) * 5 / 9 + 273.15
            else -> value
        }

        "Kelvin" -> when (toUnit) {
            "Celsius" -> value - 273.15
            "Fahrenheit" -> (value - 273.15) * 9 / 5 + 32
            else -> value
        }

        else -> value
    }
}

// Provides time conversion factors (base unit: Second)
fun convertTime(value: Double, fromUnit: String, toUnit: String) = convertWithFactors(
    value, fromUnit, toUnit, mapOf(
        "Second" to 1.0, "Minute" to 60.0, "Hour" to 3600.0, "Day" to 86400.0,
        "Week" to 604800.0, "Month" to 2629800.0, "Year" to 31557600.0
    )
)

// A preview function to see the UI in Android Studio's design view
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UnitConverterScreenPreview() {
    UnitConverterTheme {
        UnitConverterApp()
    }
}