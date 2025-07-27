package com.learnkt.unitconverter.view.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import com.learnkt.unitconverter.ui.screens.UnitConverterScreen
import com.learnkt.unitconverter.view.ui.theme.UnitConverterTheme
import com.learnkt.unitconverter.viewmodel.ConverterViewModel

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterApp() {
    val myviewModel = ConverterViewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unit Converter", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },

    ) {
        innerPadding ->
        UnitConverterScreen(
            modifier = Modifier.padding(innerPadding),
            viewModel = myviewModel
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UnitConverterAppPreview() {
    UnitConverterTheme {
        UnitConverterApp()
    }
}