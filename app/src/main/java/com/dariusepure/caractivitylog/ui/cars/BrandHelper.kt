package com.dariusepure.caractivitylog.ui.cars

import android.content.Context
import java.util.Locale

object BrandHelper {
    fun getLogoResource(context: Context, make: String): Int {
        val trimmed = make.trim()
        if (trimmed.isBlank() || trimmed.equals("OTHER", ignoreCase = true)) return 0
        
        // Normalize name to match resource names (lowercase, dots removed, spaces/special chars to underscores)
        var normalized = trimmed.lowercase(Locale.ROOT)
            .replace(" ", "_")
            .replace("-", "_")
            .replace(".", "")
            .replace("ë", "e")
            .replace("ö", "o")
            .replace("ä", "a")
            .replace("ü", "u")
            .replace("š", "s")
            .replace("ç", "c")
            .replace("é", "e")
            .replace("í", "i")
        
        // Android resources cannot start with a digit
        if (normalized.firstOrNull()?.isDigit() == true) {
            normalized = "brand_$normalized"
        }
        
        // Find resource ID by name in the "drawable" folder
        return context.resources.getIdentifier(normalized, "drawable", context.packageName)
    }
}
