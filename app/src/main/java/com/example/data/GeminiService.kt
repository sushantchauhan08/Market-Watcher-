package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig
import android.util.Log

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

// --- Moshi models for the parsed crop data from Gemini ---
@JsonClass(generateAdapter = true)
data class GeminiPricePoint(
    val label: String,
    val price: Double
)

@JsonClass(generateAdapter = true)
data class GeminiMarketOption(
    val name: String,
    val rating: Double,
    val price: Double,
    val distance: String,
    val updatedText: String,
    val badge: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCropResponse(
    val name: String,
    val category: String,
    val currentPrice: Double,
    val unit: String = "kg",
    val location: String = "Aligarh Mandi, UP",
    val changePercent: Double,
    val forecastTomorrow: Double,
    val forecastPlus3Days: Double,
    val forecastConfidence: Int,
    val trendPercentTomorrow: Double,
    val highestPrice: Double,
    val lowestPrice: Double,
    val averagePrice: Double,
    val volatility: String,
    val aiInsights: String,
    val history: List<GeminiPricePoint>,
    val nearbyMarkets: List<GeminiMarketOption>
) {
    fun toProduct(): Product {
        // High quality premium Unsplash images matching agricultural focus
        val imageMapper = mapOf(
            "wheat" to "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?auto=format&fit=crop&q=80&w=600",
            "rice" to "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&q=80&w=600",
            "mustard" to "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&q=80&w=600",
            "mango" to "https://images.unsplash.com/photo-1553279768-865429fa0078?auto=format&fit=crop&q=80&w=600",
            "apple" to "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&q=80&w=600",
            "onion" to "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&q=80&w=600",
            "potato" to "https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&q=80&w=600",
            "tomato" to "https://images.unsplash.com/photo-1595855759920-86582396756a?auto=format&fit=crop&q=80&w=600",
            "garlic" to "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&q=80&w=600",
            "ginger" to "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?auto=format&fit=crop&q=80&w=600",
            "pulse" to "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&q=80&w=600",
            "dal" to "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&q=80&w=600",
            "oil" to "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&q=80&w=600"
        )

        val lowerName = name.lowercase()
        val matchedImage = imageMapper.entries.firstOrNull { lowerName.contains(it.key) }?.value
            ?: "https://images.unsplash.com/photo-1610348725531-843dff563e2c?auto=format&fit=crop&q=80&w=600" // general healthy veggies

        return Product(
            name = name,
            category = category,
            currentPrice = currentPrice,
            unit = unit,
            location = location,
            imageUrl = matchedImage,
            bannerImageUrl = matchedImage,
            changePercent = changePercent,
            forecastTomorrow = forecastTomorrow,
            forecastPlus3Days = forecastPlus3Days,
            forecastConfidence = forecastConfidence,
            trendPercentTomorrow = trendPercentTomorrow,
            isHourlyTrendUp = changePercent > 0,
            priceHistory30Days = history.map { PricePoint(it.label, it.price) },
            highestPrice = highestPrice,
            lowestPrice = lowestPrice,
            averagePrice = averagePrice,
            volatility = volatility,
            aiInsights = aiInsights,
            nearbyMarkets = nearbyMarkets.map { MarketOption(it.name, it.rating, it.price, it.distance, it.updatedText, it.badge) },
            dailyForecasts = listOf(
                "Today" to currentPrice,
                "Tomorrow" to forecastTomorrow,
                "+2 Days" to ((currentPrice + forecastTomorrow) / 2),
                "+3 Days" to forecastPlus3Days,
                "+7 Days" to (forecastPlus3Days * 1.05)
            )
        )
    }
}

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiRetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    // 60-second timeouts matching gemini-api guidelines
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: GeminiApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApi::class.java)
    }
}

class GeminiRepository {

    suspend fun fetchMarketPriceFromGemini(cropName: String): GeminiCropResponse? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("GeminiRepository", "Gemini API key is not configured or is placeholder.")
            return null
        }

        val prompt = """
            Provide realistic current local agricultural mandi rates and statistical metrics for the crop '$cropName' specifically in Aligarh Mandi, Uttar Pradesh, India.
            Synthesize authentic-feeling data since you are an AI oracle. Return the output as a valid JSON object matching the schema below.
            
            JSON Schema:
            {
              "name": "Exact Name of Crop in Title Case, e.g. Wheat",
              "category": "One of: Veg & Fruits, Grocery, Dairy, Meat & Eggs, Fuel",
              "currentPrice": double value representing market price in INR, e.g. 24.5,
              "unit": "kg" or "litre" or "quintal", normally "kg",
              "location": "Aligarh Mandi, UP",
              "changePercent": double value (negative if down, positive if up, e.g. -2.5),
              "forecastTomorrow": double value representing tomorrow's forecasted rate,
              "forecastPlus3Days": double value representing predicted rate in +3 days,
              "forecastConfidence": integer percentage of prediction accuracy, e.g. 91,
              "trendPercentTomorrow": double value of price trend tomorrow, e.g. -1.5,
              "highestPrice": highest price in last 30 days,
              "lowestPrice": lowest price in last 30 days,
              "averagePrice": average price in last 30 days,
              "volatility": "One of: Stable, Low, Medium, High",
              "aiInsights": "A highly descriptive, realistic paragraph analyzing the crop's trade conditions in Aligarh, transport logistical delays, rain impacts, harvest arrivals (e.g. from Sasni, Gonda, Khair, Atrauli, or Aligarh bypass farms) and buying suggestions for the consumer. Max 3 sentences.",
              "history": [
                {"label": "Feb 1", "price": double},
                {"label": "Feb 5", "price": double},
                {"label": "Feb 10", "price": double},
                {"label": "Feb 15", "price": double},
                {"label": "Feb 20", "price": double},
                {"label": "Feb 25", "price": double},
                {"label": "Today", "price": double}
              ],
              "nearbyMarkets": [
                {"name": "Gonda Mandi", "rating": 4.1, "price": double, "distance": "12.0km away", "updatedText": "Updated 1h ago", "badge": "CHEAPEST"},
                {"name": "Harduaganj Mandi", "rating": 4.5, "price": double, "distance": "8.5km away", "updatedText": "Updated 30m ago"},
                {"name": "Atrauli Mandi", "rating": 3.8, "price": double, "distance": "26.0km away", "updatedText": "Updated 2h ago"}
              ]
            }
            
            Do not include any Markdown tags or comments in the JSON output outside of the json payload. Returns strictly valid, parsable JSON.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.4f
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "You are Aligarh Mandi's AI Price Oracle. Always respond with only raw, valid JSON.")))
        )

        return try {
            val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                Log.d("GeminiRepository", "Response text: $jsonText")
                val adapter = GeminiRetrofitClient.moshi.adapter(GeminiCropResponse::class.java)
                adapter.fromJson(jsonText)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("GeminiRepository", "Error calling Gemini: ${e.message}", e)
            null
        }
    }
}
