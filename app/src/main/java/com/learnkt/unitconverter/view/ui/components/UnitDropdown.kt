import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.learnkt.unitconverter.model.data.ConverterModel.unitAbbreviations

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