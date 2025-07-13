package com.learnkt.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learnkt.unitconverter.ui.theme.UnitConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitConverterTheme {
                UnitConverterApp()
            }
        }
    }
}

@Composable
fun UnitConverterApp() {
    Scaffold(
        modifier = Modifier.fillMaxSize(), content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Hello World", modifier = Modifier.padding(innerPadding)
                )
            }
        })
}

// ✅ Conversion logic function (placed outside composable)
fun convert(value: Double, fromUnit: String, toUnit: String): Double {
    val conversionFactorsToMeter = mapOf(
        "millimeter" to 0.001,
        "centimeter" to 0.01,
        "meter" to 1.0,
        "kilometer" to 1000.0,
        "feet" to 0.3048
    )

    val fromFactor = conversionFactorsToMeter[fromUnit.lowercase()]
    val toFactor = conversionFactorsToMeter[toUnit.lowercase()]

    return if (fromFactor != null && toFactor != null) {
        val meters = value * fromFactor
        meters / toFactor
    } else {
        0.0
    }
}

@Composable
@Preview(showBackground = true)
fun unitConverterAppPreview() {

    var inputValue by remember { mutableStateOf("") }
    var inputUnit by remember { mutableStateOf("") }
    var outputUnit by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var iExpanded by remember { mutableStateOf(false) }
    var oExpanded by remember { mutableStateOf(false) }



    // ✅ Automatically update result when input changes
    LaunchedEffect(inputValue, inputUnit, outputUnit) {
        val inputDouble = inputValue.toDoubleOrNull()
        if (inputDouble != null && inputUnit.isNotEmpty() && outputUnit.isNotEmpty()) {
            result = convert(inputDouble, inputUnit, outputUnit).toString()
        } else {
            result = ""
        }
    }

    @Composable
    fun DropDownname(
        expanded: Boolean,
        onDismissRequest: () -> Unit,
        onSelect: (String) -> Unit
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            val units = listOf("Centimeter", "Millimeter", "Meter", "Kilometer", "Feet")
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onSelect(unit)
                    }
                )
            }
        }
    }

    @Composable
    fun ButtonSelect(textname: String, onClickfun: () -> Unit) {
        Button(onClick = onClickfun) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = textname)
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select"
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Unit Converter", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            placeholder = { Text("Enter a Value") }
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row {
            // Input unit dropdown
            Box {
                ButtonSelect(inputUnit.ifEmpty { "Select" }) {
                    iExpanded = true
                    oExpanded = false
                }
                DropDownname(
                    expanded = iExpanded,
                    onDismissRequest = { iExpanded = false },
                    onSelect = { selectedUnit ->
                        inputUnit = selectedUnit
                    }
                )
            }

            Spacer(modifier = Modifier.width(35.dp))

            // Output unit dropdown
            Box {
                ButtonSelect(outputUnit.ifEmpty { "Select" }) {
                    oExpanded = true
                    iExpanded = false
                }
                DropDownname(
                    expanded = oExpanded,
                    onDismissRequest = { oExpanded = false },
                    onSelect = { selectedUnit ->
                        outputUnit = selectedUnit
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "Result: $result", style = MaterialTheme.typography.headlineLarge)
    }
}
