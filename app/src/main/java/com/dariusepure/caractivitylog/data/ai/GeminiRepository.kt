package com.dariusepure.caractivitylog.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.dariusepure.caractivitylog.R
import com.dariusepure.caractivitylog.domain.ScannedCarData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class GeminiRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig
) {

    init {
        remoteConfig.fetchAndActivate()
    }

    private val modelName: String
        get() = remoteConfig.getString("gemini_model_name").ifBlank { "gemini-3.5-flash-lite" }

    private val requestOptions: RequestOptions
        get() = RequestOptions(timeout = remoteConfig.getLong("gemini_timeout_seconds").seconds)

    private val temperature: Float
        get() = remoteConfig.getDouble("gemini_temperature").toFloat()

    private val systemPrompt: String
        get() = remoteConfig.getString("gemini_prompt")

    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private val updateCarTools = Tool(
        functionDeclarations = listOf(
            defineFunction(
                name = "update_car_spec",
                description = "Updates a specific technical specification of the car.",
                parameters = listOf(
                    Schema.str("field", "The technical field to update. Valid fields: make, model, vin, year, engineSize, fuelType, fuelSystem, color, power, torque, engineCode, engineLayout, length, width, height, wheelbase, trackWidth, emissionStandard, aspiration, fuelTankCapacity, batteryCapacity, drivetrain, gearboxType, gears, frontSuspension, rearSuspension, frontBrakes, rearBrakes, vehicleType, manufacturingCountry, topSpeed, weight, numberOfSeats, numberOfCylinders, valvesPerCylinder, numberOfDoors, bootSpace, tireWidth, tireAspectRatio, tireDiameter."),
                    Schema.str("value", "The new value for the field. For dropdown fields, you MUST pick one of the standard English values provided in instructions.")
                ),
                requiredParameters = listOf("field", "value")
            ),
            defineFunction(
                name = "update_car_mileage",
                description = "Updates the car's current mileage (odometer reading).",
                parameters = listOf(
                    Schema.str("km", "The current mileage in kilometers.")
                ),
                requiredParameters = listOf("km")
            )
        )
    )

    suspend fun scanRegistrationCertificate(bitmap: Bitmap): Result<ScannedCarData> {
        return try {
            val prompt = """
                Extract technical details from this vehicle document (registration certificate, invoice, insurance, or technical sheet).
                Analyze the document and look for these fields:
                - make, model, vin (MUST be 17 chars), year (4 digits), fuelType, engineSize (cc), power (hp or kW), torque (Nm), color, gears, registrationPlate.
                
                CRITICAL VALIDATION:
                1. Verify VIN format: must be 17 characters, only letters and digits (excluding I, O, Q).
                2. Verify Year: must be a realistic year (e.g., 1900-2026).
                3. Verify Engine Size: must be in cubic centimeters (cc).
                4. If a value is unreadable, illogical, or not found, return null for that field.
                
                Return ONLY a JSON object with these keys: 
                make, model, vin, year, fuelType, engineSize, power, powerUnit, torque, color, 
                registrationPlate, numberOfSeats, numberOfDoors, weight, engineCode, 
                emissionStandard, gearboxType, gears, drivetrain, fuelTankCapacity, topSpeed.
                
                Standard fuelType: Petrol, Diesel, Electric, Hybrid, LPG.
                Standard powerUnit: 'hp'. If kW is found, convert to hp (kW * 1.36).
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val scanModel = GenerativeModel(
                modelName = modelName,
                apiKey = context.getString(R.string.gemini_api_key),
                generationConfig = generationConfig {
                    temperature = this@GeminiRepository.temperature
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
                Extract technical details from this vehicle document (registration certificate, invoice, insurance, or technical sheet).
                Analyze the document and look for these fields:
                - make, model, vin (MUST be 17 chars), year (4 digits), fuelType, engineSize (cc), power (hp or kW), torque (Nm), color, gears, registrationPlate.
                
                CRITICAL VALIDATION:
                1. Verify VIN format: must be 17 characters, only letters and digits (excluding I, O, Q).
                2. Verify Year: must be a realistic year (e.g., 1900-2026).
                3. Verify Engine Size: must be in cubic centimeters (cc).
                4. If a value is unreadable, illogical, or not found, return null for that field.
                
                Return ONLY a JSON object with these keys: 
                make, model, vin, year, fuelType, engineSize, power, powerUnit, torque, color, 
                registrationPlate, numberOfSeats, numberOfDoors, weight, engineCode, 
                emissionStandard, gearboxType, gears, drivetrain, fuelTankCapacity, topSpeed.
                
                Standard fuelType: Petrol, Diesel, Electric, Hybrid, LPG.
                Standard powerUnit: 'hp'. If kW is found, convert to hp (kW * 1.36).
            """.trimIndent()

            val inputContent = content {
                blob(mimeType, bytes)
                text(prompt)
            }

            val scanModel = GenerativeModel(
                modelName = modelName,
                apiKey = context.getString(R.string.gemini_api_key),
                generationConfig = generationConfig {
                    temperature = this@GeminiRepository.temperature
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
    ): com.google.ai.client.generativeai.type.GenerateContentResponse {
        val diagnosisModel = GenerativeModel(
            modelName = modelName,
            apiKey = context.getString(R.string.gemini_api_key),
            tools = listOf(updateCarTools),
            generationConfig = generationConfig {
                temperature = this@GeminiRepository.temperature
            },
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
        
        val finalizedPrompt = systemPrompt.replace("{{context}}", carContext)

        return chat.sendMessage(content("user") { text("$finalizedPrompt\n\nUser: $prompt") })
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
