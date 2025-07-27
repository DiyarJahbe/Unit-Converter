package com.learnkt.unitconverter.model.data

enum class ConversionCategory {
    LENGTH, WEIGHT, VOLUME, TEMPERATURE, TIME
}

// Holds all data and conversion logic, acting as our "Model"
object ConverterModel {
    // Maps each conversion category to a list of its available units
    val unitMap = mapOf(
        ConversionCategory.LENGTH to listOf("Millimeter", "Centimeter", "Meter", "Kilometer", "Inch", "Feet", "Yard", "Mile"),
        ConversionCategory.WEIGHT to listOf("Milligram", "Gram", "Kilogram", "Pound", "Ounce", "Ton"),
        ConversionCategory.VOLUME to listOf("Milliliter", "Liter", "Cubic Meter", "Teaspoon", "Tablespoon", "Cup", "Pint", "Quart", "Gallon"),
        ConversionCategory.TEMPERATURE to listOf("Celsius", "Fahrenheit", "Kelvin"),
        ConversionCategory.TIME to listOf("Second", "Minute", "Hour", "Day", "Week", "Month", "Year")
    )

    // Maps the full unit names to their shorter abbreviations
    val unitAbbreviations = mapOf(
        "Millimeter" to "mm", "Centimeter" to "cm", "Meter" to "m", "Kilometer" to "km", "Inch" to "in",
        "Feet" to "ft", "Yard" to "yd", "Mile" to "mi", "Milligram" to "mg", "Gram" to "g", "Kilogram" to "kg",
        "Pound" to "lb", "Ounce" to "oz", "Ton" to "t", "Milliliter" to "ml", "Liter" to "L", "Cubic Meter" to "m³",
        "Teaspoon" to "tsp", "Tablespoon" to "tbsp", "Cup" to "cup", "Pint" to "pt", "Quart" to "qt", "Gallon" to "gal",
        "Celsius" to "°C", "Fahrenheit" to "°F", "Kelvin" to "K", "Second" to "s", "Minute" to "min", "Hour" to "hr",
        "Day" to "day", "Week" to "wk", "Month" to "mo", "Year" to "yr"
    )

    // Main conversion function that calls the correct specific conversion logic
    fun convert(value: Double, fromUnit: String, toUnit: String, category: ConversionCategory): Double =
        when (category) {
            ConversionCategory.LENGTH -> convertLength(value, fromUnit, toUnit)
            ConversionCategory.WEIGHT -> convertWeight(value, fromUnit, toUnit)
            ConversionCategory.VOLUME -> convertVolume(value, fromUnit, toUnit)
            ConversionCategory.TEMPERATURE -> convertTemperature(value, fromUnit, toUnit)
            ConversionCategory.TIME -> convertTime(value, fromUnit, toUnit)
        }

    // A generic conversion function that works for units with a common base
    private fun convertWithFactors(value: Double, fromUnit: String, toUnit: String, factors: Map<String, Double>): Double {
        if (fromUnit == toUnit) return value
        val fromFactor = factors[fromUnit] ?: return 0.0
        val toFactor = factors[toUnit] ?: return 0.0
        return value * fromFactor / toFactor
    }

    // Provides length conversion factors (base unit: Meter)
    private fun convertLength(value: Double, fromUnit: String, toUnit: String) = convertWithFactors(value, fromUnit, toUnit,
        mapOf("Millimeter" to 0.001, "Centimeter" to 0.01, "Meter" to 1.0, "Kilometer" to 1000.0, "Inch" to 0.0254, "Feet" to 0.3048, "Yard" to 0.9144, "Mile" to 1609.34))

    // Provides weight conversion factors (base unit: Kilogram)
    private fun convertWeight(value: Double, fromUnit: String, toUnit: String) = convertWithFactors(value, fromUnit, toUnit,
        mapOf("Milligram" to 0.000001, "Gram" to 0.001, "Kilogram" to 1.0, "Pound" to 0.453592, "Ounce" to 0.0283495, "Ton" to 1000.0))

    // Provides volume conversion factors (base unit: Liter)
    private fun convertVolume(value: Double, fromUnit: String, toUnit: String) = convertWithFactors(value, fromUnit, toUnit,
        mapOf("Milliliter" to 0.001, "Liter" to 1.0, "Cubic Meter" to 1000.0, "Teaspoon" to 0.00492892, "Tablespoon" to 0.0147868, "Cup" to 0.24, "Pint" to 0.473176, "Quart" to 0.946353, "Gallon" to 3.78541))

    // Handles temperature conversion
    private fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
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
    private fun convertTime(value: Double, fromUnit: String, toUnit: String) = convertWithFactors(value, fromUnit, toUnit,
        mapOf("Second" to 1.0, "Minute" to 60.0, "Hour" to 3600.0, "Day" to 86400.0, "Week" to 604800.0, "Month" to 2629800.0, "Year" to 31557600.0))
}