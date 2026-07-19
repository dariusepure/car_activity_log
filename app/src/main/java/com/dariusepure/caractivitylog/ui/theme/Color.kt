package com.dariusepure.caractivitylog.ui.theme

import androidx.compose.ui.graphics.Color

// --- Tema "Dark Garage" (Sport / Agresiv) ---

// Fundal și Suprafete
val GarageMidnight = Color(0xFF121212)
val GarageCard = Color(0xFF2A2A2A)

// Accente Sport
val RacingRed = Color(0xFFFF3B30)
val CyberGreen = Color(0xFF00E676)
val EditBlue = Color(0xFF2196F3)

// Text
val GarageWhite = Color(0xFFFFFFFF)
val GarageGreyText = Color(0xFFB0B0B0)

// Mapare pentru Dark Theme
val PrimaryDark = CyberGreen // Save button will be Green Neon
val OnPrimaryDark = GarageMidnight
val PrimaryContainerDark = Color(0xFF00331A)
val OnPrimaryContainerDark = GarageWhite

val SecondaryDark = EditBlue // Edit button / Secondary actions will be Blue
val OnSecondaryDark = GarageWhite
val SecondaryContainerDark = Color(0xFF002244)
val OnSecondaryContainerDark = GarageWhite

val BackgroundDark = GarageMidnight
val OnBackgroundDark = GarageWhite
val SurfaceDark = GarageCard
val OnSurfaceDark = GarageWhite

val ErrorDark = RacingRed
val OnErrorDark = GarageWhite

// Mapare pentru Light Theme (Păstrăm un stil curat dar adaptat)
val PrimaryLight = Color(0xFFD32F2F) // Un roșu mai închis pentru contrast pe alb
val OnPrimaryLight = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF121212)
val SurfaceLight = Color(0xFFF5F5F5)
val OnSurfaceLight = Color(0xFF121212)
