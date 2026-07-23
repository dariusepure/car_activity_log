package com.dariusepure.caractivitylog.ui.cars

import java.util.Locale

object BrandHelper {
    fun getLogoUrl(make: String): String? {
        val trimmed = make.trim()
        if (trimmed.isBlank() || trimmed.equals("OTHER", ignoreCase = true)) return null
        
        // Normalize name: lowercase, remove dots, replace spaces/special chars with hyphens
        val normalized = trimmed.lowercase(Locale.ROOT)
            .replace(" ", "-")
            .replace(".", "")
            .replace("ë", "e")
            .replace("ö", "o")
            .replace("ä", "a")
            .replace("ü", "u")
            .replace("š", "s")
            .replace("ç", "c")
            .replace("é", "e")
            .replace("í", "i")
        
        // Using a more stable and verified repository structure
        return "https://raw.githubusercontent.com/filippofilip95/car-logos-dataset/master/logos/optimized/$normalized.png"
    }
}
