package com.dariusepure.caractivitylog.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.dariusepure.caractivitylog.R
import com.dariusepure.caractivitylog.domain.ScannedCarData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class GeminiRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val requestOptions = RequestOptions(timeout = 30.seconds)
    private val modelName = "gemini-3.5-flash-lite"

    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    suspend fun scanRegistrationCertificate(bitmap: Bitmap): Result<ScannedCarData> {
        return try {
            val prompt = """
                Extract technical details from this vehicle registration certificate (talon auto / carte identitate).
                Look for both labels and standard EU codes:
                - Make (Marca, D.1)
                - Model (Varianta/Denumire comerciala, D.3)
                - VIN (Serie sasiu, E) - MUST BE 17 CHARACTERS
                - Year (An fabricatie, or from first registration date B)
                - Fuel Type (Combustibil, P.3)
                - Engine Size (Capacitate cilindrica, P.1)
                - Power (Putere, P.2 - usually in kW, convert to hp if possible or specify)
                - Engine Code (Serie motor, P.5)
                - Emission Standard (Norma poluare, V.9, e.g., Euro 6)
                - Color (Culoare, R)
                - Registration Plate (Numar inmatriculare, A)
                - Number of Seats (Locuri, S.1)
                - Weight (Masa, G)
                - Torque, Gearbox Type, Drivetrain, Fuel Tank, Top Speed (if mentioned in notes or technical specs).
                
                Return ONLY a JSON object with these keys: 
                make, model, vin, year, fuelType, engineSize, power, powerUnit, torque, color, 
                registrationPlate, numberOfSeats, numberOfDoors, weight, engineCode, 
                emissionStandard, gearboxType, drivetrain, fuelTankCapacity, topSpeed.
                
                CRITICAL INSTRUCTIONS:
                1. If a value is not found, use null. 
                2. For fuelType use one of: Petrol, Diesel, Electric, Hybrid, LPG.
                3. For numeric fields (year, engineSize, power, torque, weight, numberOfSeats, numberOfDoors, fuelTankCapacity, topSpeed), return ONLY the number (e.g., 150), never include units or text.
                4. Standard powerUnit is 'hp'. If you find kW (P.2), multiply by 1.36 and return the result as an integer in 'power'.
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            // Use the model requested by the user
            val scanModel = GenerativeModel(
                modelName = modelName,
                apiKey = context.getString(R.string.gemini_api_key),
                generationConfig = generationConfig {
                    temperature = 0.1f
                },
                requestOptions = requestOptions
            )

            val response = scanModel.generateContent(inputContent)
            val fullText = response.text ?: throw Exception("Empty response from AI")
            
            val jsonText = extractJson(fullText)
            
            val data = json.decodeFromString<ScannedCarData>(jsonText)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun scanDocument(uri: Uri, mimeType: String): Result<ScannedCarData> {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw Exception("Could not read file")

            val prompt = """
                Extract technical details from this vehicle registration certificate (talon auto / carte identitate).
                Look for both labels and standard EU codes:
                - Make (Marca, D.1)
                - Model (Varianta/Denumire comerciala, D.3)
                - VIN (Serie sasiu, E) - MUST BE 17 CHARACTERS
                - Year (An fabricatie, or from first registration date B)
                - Fuel Type (Combustibil, P.3)
                - Engine Size (Capacitate cilindrica, P.1)
                - Power (Putere, P.2 - usually in kW, convert to hp if possible or specify)
                - Engine Code (Serie motor, P.5)
                - Emission Standard (Norma poluare, V.9, e.g., Euro 6)
                - Color (Culoare, R)
                - Registration Plate (Numar inmatriculare, A)
                - Number of Seats (Locuri, S.1)
                - Weight (Masa, G)
                - Torque, Gearbox Type, Drivetrain, Fuel Tank, Top Speed (if mentioned in notes or technical specs).
                
                Return ONLY a JSON object with these keys: 
                make, model, vin, year, fuelType, engineSize, power, powerUnit, torque, color, 
                registrationPlate, numberOfSeats, numberOfDoors, weight, engineCode, 
                emissionStandard, gearboxType, drivetrain, fuelTankCapacity, topSpeed.
                
                CRITICAL INSTRUCTIONS:
                1. If a value is not found, use null. 
                2. For fuelType use one of: Petrol, Diesel, Electric, Hybrid, LPG.
                3. For numeric fields (year, engineSize, power, torque, weight, numberOfSeats, numberOfDoors, fuelTankCapacity, topSpeed), return ONLY the number (e.g., 150), never include units or text.
                4. Standard powerUnit is 'hp'. If you find kW (P.2), multiply by 1.36 and return the result as an integer in 'power'.
            """.trimIndent()

            val inputContent = content {
                blob(mimeType, bytes)
                text(prompt)
            }

            val scanModel = GenerativeModel(
                modelName = modelName,
                apiKey = context.getString(R.string.gemini_api_key),
                generationConfig = generationConfig {
                    temperature = 0.1f
                },
                requestOptions = requestOptions
            )

            val response = scanModel.generateContent(inputContent)
            val fullText = response.text ?: throw Exception("Empty response from AI")
            
            val jsonText = extractJson(fullText)
            val data = json.decodeFromString<ScannedCarData>(jsonText)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDiagnosisResponse(
        prompt: String,
        carContext: String,
        history: List<com.dariusepure.caractivitylog.ui.cars.ChatMessage>
    ): Result<String> {
        return try {
            val diagnosisModel = GenerativeModel(
                modelName = modelName,
                apiKey = context.getString(R.string.gemini_api_key),
                requestOptions = requestOptions
            )

            // CRITICAL: history MUST start with user and alternate roles.
            val validatedHistory = history
                .dropWhile { !it.isUser } // Must start with user
                .let { h ->
                    // History must end with a model response for startChat(history) to work with a user sendMessage
                    if (h.isNotEmpty() && h.size % 2 != 0) h.dropLast(1) else h
                }
                .map { 
                    content(if (it.isUser) "user" else "model") { text(it.text) }
                }

            val chat = diagnosisModel.startChat(history = validatedHistory)
            
            val fullPrompt = """
                System: You are an expert car mechanic AI. Context: $carContext
                User: $prompt
            """.trimIndent()

            val response = chat.sendMessage(fullPrompt)
            Result.success(response.text ?: "No response.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractJson(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1)
        }
        return text
    }

}
