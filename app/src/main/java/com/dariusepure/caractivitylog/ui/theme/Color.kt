package com.dariusepure.caractivitylog.ui.theme

import androidx.compose.ui.graphics.Color

// --- Tema "Dark Garage" (Sport / Agresiv) ---

// Fundal și Suprafete
val GarageMidnight = Color(0xFF000000)
val GarageCard = Color(0xFF1A1A1A)

// Accente Albastre
val BlueLight = Color(0xFF2196F3) // Albastru deschis pentru Light Mode
val BlueDark = Color(0xFF1565C0)  // Albastru mai închis pentru Dark Mode
val BlueSky = Color(0xFFE3F2FD)   // Container deschis
val BlueDeep = Color(0xFF0D47A1)  // Container închis

// Text
val GarageWhite = Color(0xFFFFFFFF)
val GarageGreyText = Color(0xFFB0B0B0)

// Mapare pentru Dark Theme
val PrimaryDark = BlueDark
val OnPrimaryDark = GarageWhite
val PrimaryContainerDark = Color(0xFF0A192F)
val OnPrimaryContainerDark = GarageWhite

val SecondaryDark = Color(0xFF64B5F6)
val OnSecondaryDark = GarageWhite
val SecondaryContainerDark = Color(0xFF0D47A1)
val OnSecondaryContainerDark = GarageWhite

val BackgroundDark = GarageMidnight
val OnBackgroundDark = GarageWhite
val SurfaceDark = GarageCard
val OnSurfaceDark = GarageWhite

val ErrorDark = Color(0xFFFF3B30) // Red for error
val OnErrorDark = GarageWhite

// Mapare pentru Light Theme
val PrimaryLight = BlueLight
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = BlueSky
val OnPrimaryContainerLight = BlueDark

val BackgroundLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF000000)
val SurfaceLight = Color(0xFFF5F9FF)
val OnSurfaceLight = Color(0xFF000000)
